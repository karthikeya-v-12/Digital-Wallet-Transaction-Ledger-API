package com.wallet.ledger.service;

import com.wallet.ledger.dto.CreateWalletRequest;
import com.wallet.ledger.dto.WalletResponse;
import com.wallet.ledger.exception.WalletNotFoundException;
import com.wallet.ledger.model.Wallet;
import com.wallet.ledger.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        BigDecimal initialBalance = request.getInitialBalance() != null 
                ? request.getInitialBalance() 
                : BigDecimal.ZERO;

        Wallet wallet = Wallet.builder()
                .userId(request.getUserId())
                .currency(request.getCurrency().toUpperCase())
                .balance(initialBalance)
                .build();

        Wallet saved = walletRepository.save(wallet);
        log.info("Created new wallet id={} for userId={}", saved.getId(), saved.getUserId());
        return WalletResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletById(UUID id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException(id));
        return WalletResponse.fromEntity(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> getWalletsByUserId(String userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(WalletResponse::fromEntity)
                .toList();
    }
}
