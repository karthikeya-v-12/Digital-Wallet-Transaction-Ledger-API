package com.wallet.ledger.dto;

import com.wallet.ledger.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionRequest {

    @NotBlank(message = "idempotencyKey is required")
    private String idempotencyKey;

    private String transactionRef;

    private UUID sourceWalletId;

    private UUID targetWalletId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.0001", message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "transaction type is required")
    private TransactionType type;

    public TransactionRequest() {
    }

    public TransactionRequest(String idempotencyKey, String transactionRef, UUID sourceWalletId, UUID targetWalletId, BigDecimal amount, TransactionType type) {
        this.idempotencyKey = idempotencyKey;
        this.transactionRef = transactionRef;
        this.sourceWalletId = sourceWalletId;
        this.targetWalletId = targetWalletId;
        this.amount = amount;
        this.type = type;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String idempotencyKey;
        private String transactionRef;
        private UUID sourceWalletId;
        private UUID targetWalletId;
        private BigDecimal amount;
        private TransactionType type;

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder transactionRef(String transactionRef) {
            this.transactionRef = transactionRef;
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

        public TransactionRequest build() {
            return new TransactionRequest(idempotencyKey, transactionRef, sourceWalletId, targetWalletId, amount, type);
        }
    }
}
