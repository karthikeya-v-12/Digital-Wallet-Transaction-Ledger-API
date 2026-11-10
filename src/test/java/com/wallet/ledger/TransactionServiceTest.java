package com.wallet.ledger;

import com.wallet.ledger.dto.CreateWalletRequest;
import com.wallet.ledger.dto.TransactionRequest;
import com.wallet.ledger.dto.TransactionResponse;
import com.wallet.ledger.dto.WalletResponse;
import com.wallet.ledger.exception.InsufficientBalanceException;
import com.wallet.ledger.exception.InvalidTransactionException;
import com.wallet.ledger.model.TransactionStatus;
import com.wallet.ledger.model.TransactionType;
import com.wallet.ledger.service.TransactionService;
import com.wallet.ledger.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Should successfully transfer funds between two wallets and update balances atomically")
    void testSuccessfulTransfer() {
        WalletResponse alice = walletService.createWallet(CreateWalletRequest.builder()
                .userId("alice")
                .currency("USD")
                .initialBalance(new BigDecimal("100.0000"))
                .build());

        WalletResponse bob = walletService.createWallet(CreateWalletRequest.builder()
                .userId("bob")
                .currency("USD")
                .initialBalance(new BigDecimal("20.0000"))
                .build());

        TransactionRequest request = TransactionRequest.builder()
                .idempotencyKey("idem-transfer-101")
                .sourceWalletId(alice.getId())
                .targetWalletId(bob.getId())
                .amount(new BigDecimal("35.5000"))
                .type(TransactionType.TRANSFER)
                .build();

        TransactionResponse tx = transactionService.processTransaction(request);

        assertNotNull(tx.getId());
        assertEquals(TransactionStatus.SUCCESS, tx.getStatus());
        assertEquals(new BigDecimal("35.5000"), tx.getAmount());

        WalletResponse updatedAlice = walletService.getWalletById(alice.getId());
        WalletResponse updatedBob = walletService.getWalletById(bob.getId());

        assertEquals(0, new BigDecimal("64.5000").compareTo(updatedAlice.getBalance()));
        assertEquals(0, new BigDecimal("55.5000").compareTo(updatedBob.getBalance()));
    }

    @Test
    @DisplayName("Should enforce idempotency: repeating the same idempotency key returns the cached transaction without double debit")
    void testIdempotencyProtection() {
        WalletResponse source = walletService.createWallet(CreateWalletRequest.builder()
                .userId("charlie")
                .currency("EUR")
                .initialBalance(new BigDecimal("200.0000"))
                .build());

        WalletResponse target = walletService.createWallet(CreateWalletRequest.builder()
                .userId("david")
                .currency("EUR")
                .initialBalance(new BigDecimal("50.0000"))
                .build());

        String key = "unique-idempotency-key-777";
        TransactionRequest request = TransactionRequest.builder()
                .idempotencyKey(key)
                .sourceWalletId(source.getId())
                .targetWalletId(target.getId())
                .amount(new BigDecimal("50.0000"))
                .type(TransactionType.TRANSFER)
                .build();

        TransactionResponse firstResponse = transactionService.processTransaction(request);
        TransactionResponse secondResponse = transactionService.processTransaction(request);

        assertEquals(firstResponse.getId(), secondResponse.getId());
        assertEquals(firstResponse.getTransactionRef(), secondResponse.getTransactionRef());

        // Ensure balance was deducted exactly ONCE
        WalletResponse sourceAfter = walletService.getWalletById(source.getId());
        assertEquals(0, new BigDecimal("150.0000").compareTo(sourceAfter.getBalance()));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when wallet lacks funds")
    void testInsufficientBalance() {
        WalletResponse poorWallet = walletService.createWallet(CreateWalletRequest.builder()
                .userId("eve")
                .currency("USD")
                .initialBalance(new BigDecimal("10.0000"))
                .build());

        WalletResponse targetWallet = walletService.createWallet(CreateWalletRequest.builder()
                .userId("frank")
                .currency("USD")
                .initialBalance(BigDecimal.ZERO)
                .build());

        TransactionRequest request = TransactionRequest.builder()
                .idempotencyKey("fail-transfer-999")
                .sourceWalletId(poorWallet.getId())
                .targetWalletId(targetWallet.getId())
                .amount(new BigDecimal("500.0000"))
                .type(TransactionType.TRANSFER)
                .build();

        assertThrows(InsufficientBalanceException.class, () -> transactionService.processTransaction(request));
    }

    @Test
    @DisplayName("Should reject cross-currency transfers")
    void testCrossCurrencyRejection() {
        WalletResponse usdWallet = walletService.createWallet(CreateWalletRequest.builder()
                .userId("user_usd")
                .currency("USD")
                .initialBalance(new BigDecimal("100.0000"))
                .build());

        WalletResponse eurWallet = walletService.createWallet(CreateWalletRequest.builder()
                .userId("user_eur")
                .currency("EUR")
                .initialBalance(new BigDecimal("100.0000"))
                .build());

        TransactionRequest request = TransactionRequest.builder()
                .idempotencyKey("cross-currency-test")
                .sourceWalletId(usdWallet.getId())
                .targetWalletId(eurWallet.getId())
                .amount(new BigDecimal("10.0000"))
                .type(TransactionType.TRANSFER)
                .build();

        assertThrows(InvalidTransactionException.class, () -> transactionService.processTransaction(request));
    }
}
