package com.wallet.ledger.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(UUID walletId, BigDecimal balance, BigDecimal requestedAmount) {
        super(String.format("Wallet %s has insufficient balance (%s) for requested amount (%s)",
                walletId, balance, requestedAmount));
    }

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
