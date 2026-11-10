package com.wallet.ledger.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "ledger_transactions",
    indexes = {
        @Index(name = "idx_tx_ref", columnList = "transaction_ref", unique = true),
        @Index(name = "idx_tx_idempotency_key", columnList = "idempotency_key", unique = true),
        @Index(name = "idx_tx_source_wallet", columnList = "source_wallet_id"),
        @Index(name = "idx_tx_target_wallet", columnList = "target_wallet_id"),
        @Index(name = "idx_tx_timestamp", columnList = "timestamp")
    }
)
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "transaction_ref", nullable = false, unique = true, updatable = false)
    private String transactionRef;

    @NotBlank
    @Column(name = "idempotency_key", nullable = false, unique = true, updatable = false)
    private String idempotencyKey;

    @Column(name = "source_wallet_id", updatable = false)
    private UUID sourceWalletId;

    @Column(name = "target_wallet_id", updatable = false)
    private UUID targetWalletId;

    @NotNull
    @DecimalMin(value = "0.0001", message = "Amount must be greater than zero")
    @Column(name = "amount", precision = 19, scale = 4, nullable = false, updatable = false)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false, updatable = false)
    private TransactionType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false, updatable = false)
    private TransactionStatus status;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    public LedgerTransaction() {
    }

    public LedgerTransaction(UUID id, String transactionRef, String idempotencyKey, UUID sourceWalletId, UUID targetWalletId, BigDecimal amount, TransactionType type, TransactionStatus status, Instant timestamp) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LedgerTransaction that = (LedgerTransaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

        public LedgerTransaction build() {
            return new LedgerTransaction(id, transactionRef, idempotencyKey, sourceWalletId, targetWalletId, amount, type, status, timestamp);
        }
    }
}
