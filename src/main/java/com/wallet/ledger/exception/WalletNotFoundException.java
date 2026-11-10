package com.wallet.ledger.exception;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(UUID id) {
        super("Wallet not found with id: " + id);
    }

    public WalletNotFoundException(String message) {
        super(message);
    }
}
