package com.wallet.ledger.service;

import com.wallet.ledger.dto.TransactionRequest;
import com.wallet.ledger.dto.TransactionResponse;
import com.wallet.ledger.exception.InsufficientBalanceException;
import com.wallet.ledger.exception.InvalidTransactionException;
import com.wallet.ledger.exception.WalletNotFoundException;
import com.wallet.ledger.model.LedgerTransaction;
import com.wallet.ledger.model.TransactionStatus;
import com.wallet.ledger.model.TransactionType;
import com.wallet.ledger.model.Wallet;
import com.wallet.ledger.repository.LedgerTransactionRepository;
import com.wallet.ledger.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final WalletRepository walletRepository;
    private final LedgerTransactionRepository transactionRepository;

    public TransactionService(WalletRepository walletRepository, LedgerTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        // 1. Idempotency Check: Return existing transaction if already processed
        Optional<LedgerTransaction> existingTx = transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingTx.isPresent()) {
            log.info("Idempotent hit for key: {}. Returning existing transaction id: {}",
                    request.getIdempotencyKey(), existingTx.get().getId());
            return TransactionResponse.fromEntity(existingTx.get());
        }

        String transactionRef = (request.getTransactionRef() != null && !request.getTransactionRef().isBlank())
                ? request.getTransactionRef()
                : "TXN-" + UUID.randomUUID();

        // 2. Validate and Execute according to Transaction Type
        return switch (request.getType()) {
            case TRANSFER -> executeTransfer(request, transactionRef);
            case DEBIT -> executeDebit(request, transactionRef);
            case CREDIT -> executeCredit(request, transactionRef);
        };
    }

    private TransactionResponse executeTransfer(TransactionRequest request, String ref) {
        if (request.getSourceWalletId() == null || request.getTargetWalletId() == null) {
            throw new InvalidTransactionException("Both sourceWalletId and targetWalletId are required for TRANSFER");
        }
        if (request.getSourceWalletId().equals(request.getTargetWalletId())) {
            throw new InvalidTransactionException("Source and target wallet cannot be the same for TRANSFER");
        }

        Wallet source = walletRepository.findById(request.getSourceWalletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getSourceWalletId()));
        Wallet target = walletRepository.findById(request.getTargetWalletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getTargetWalletId()));

        if (!source.getCurrency().equalsIgnoreCase(target.getCurrency())) {
            throw new InvalidTransactionException(String.format(
                    "Cross-currency transfers not supported. Source: %s, Target: %s",
                    source.getCurrency(), target.getCurrency()));
        }

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(source.getId(), source.getBalance(), request.getAmount());
        }

        // Apply mutations
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        target.setBalance(target.getBalance().add(request.getAmount()));

        walletRepository.save(source);
        walletRepository.save(target);

        // Record immutable ledger entry
        LedgerTransaction tx = LedgerTransaction.builder()
                .transactionRef(ref)
                .idempotencyKey(request.getIdempotencyKey())
                .sourceWalletId(source.getId())
                .targetWalletId(target.getId())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .build();

        LedgerTransaction savedTx = transactionRepository.save(tx);
        log.info("Transfer processed successfully: ref={}, amount={}, from={}, to={}",
                ref, request.getAmount(), source.getId(), target.getId());

        return TransactionResponse.fromEntity(savedTx);
    }

    private TransactionResponse executeDebit(TransactionRequest request, String ref) {
        if (request.getSourceWalletId() == null) {
            throw new InvalidTransactionException("sourceWalletId is required for DEBIT");
        }

        Wallet source = walletRepository.findById(request.getSourceWalletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getSourceWalletId()));

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(source.getId(), source.getBalance(), request.getAmount());
        }

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        walletRepository.save(source);

        LedgerTransaction tx = LedgerTransaction.builder()
                .transactionRef(ref)
                .idempotencyKey(request.getIdempotencyKey())
                .sourceWalletId(source.getId())
                .targetWalletId(null)
                .amount(request.getAmount())
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.SUCCESS)
                .build();

        LedgerTransaction savedTx = transactionRepository.save(tx);
        log.info("Debit processed successfully: ref={}, amount={}, wallet={}", ref, request.getAmount(), source.getId());
        return TransactionResponse.fromEntity(savedTx);
    }

    private TransactionResponse executeCredit(TransactionRequest request, String ref) {
        if (request.getTargetWalletId() == null) {
            throw new InvalidTransactionException("targetWalletId is required for CREDIT");
        }

        Wallet target = walletRepository.findById(request.getTargetWalletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getTargetWalletId()));

        target.setBalance(target.getBalance().add(request.getAmount()));
        walletRepository.save(target);

        LedgerTransaction tx = LedgerTransaction.builder()
                .transactionRef(ref)
                .idempotencyKey(request.getIdempotencyKey())
                .sourceWalletId(null)
                .targetWalletId(target.getId())
                .amount(request.getAmount())
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SUCCESS)
                .build();

        LedgerTransaction savedTx = transactionRepository.save(tx);
        log.info("Credit processed successfully: ref={}, amount={}, wallet={}", ref, request.getAmount(), target.getId());
        return TransactionResponse.fromEntity(savedTx);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id) {
        LedgerTransaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with id: " + id));
        return TransactionResponse.fromEntity(tx);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByRef(String ref) {
        LedgerTransaction tx = transactionRepository.findByTransactionRef(ref)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with reference: " + ref));
        return TransactionResponse.fromEntity(tx);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByWallet(UUID walletId) {
        return transactionRepository.findBySourceWalletIdOrTargetWalletIdOrderByTimestampDesc(walletId, walletId)
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }
}
