package com.wallet.ledger.dto;

import com.wallet.ledger.model.LedgerTransaction;
import com.wallet.ledger.model.TransactionStatus;
import com.wallet.ledger.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionResponse {

    private UUID id;
    private String transactionRef;
    private String idempotencyKey;
    private UUID sourceWalletId;
    private UUID targetWalletId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private Instant timestamp;

    public TransactionResponse() {
    }

    public TransactionResponse(UUID id, String transactionRef, String idempotencyKey, UUID sourceWalletId, UUID targetWalletId, BigDecimal amount, TransactionType type, TransactionStatus status, Instant timestamp) {
        this.id = id;
        this.transactionRef = transactionRef;
        this.idempotencyKey = idempotencyKey;
        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
    }

    public static TransactionResponse fromEntity(LedgerTransaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .transactionRef(tx.getTransactionRef())
                .idempotencyKey(tx.getIdempotencyKey())
                .sourceWalletId(tx.getSourceWalletId())
                .targetWalletId(tx.getTargetWalletId())
                .amount(tx.getAmount())
                .type(tx.getType())
                .status(tx.getStatus())
                .timestamp(tx.getTimestamp())
                .build();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getSourceWalletId() {
        return sourceWalletId;
    }

    public void setSourceWalletId(UUID sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }

    public UUID getTargetWalletId() {
        return targetWalletId;
    }

    public void setTargetWalletId(UUID targetWalletId) {
        this.targetWalletId = targetWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String transactionRef;
        private String idempotencyKey;
        private UUID sourceWalletId;
        private UUID targetWalletId;
        private BigDecimal amount;
        private TransactionType type;
        private TransactionStatus status;
        private Instant timestamp;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder transactionRef(String transactionRef) {
            this.transactionRef = transactionRef;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder sourceWalletId(UUID sourceWalletId) {
            this.sourceWalletId = sourceWalletId;
            return this;
        }

        public Builder targetWalletId(UUID targetWalletId) {
            this.targetWalletId = targetWalletId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(id, transactionRef, idempotencyKey, sourceWalletId, targetWalletId, amount, type, status, timestamp);
        }
    }
}
