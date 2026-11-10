package com.wallet.ledger.exception;

public class DuplicateIdempotencyKeyException extends RuntimeException {
    public DuplicateIdempotencyKeyException(String key) {
        super("Transaction with idempotencyKey '" + key + "' has already been processed.");
    }
}
