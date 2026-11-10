package com.wallet.ledger.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
    name = "wallets",
    indexes = {
        @Index(name = "idx_wallets_user_id", columnList = "user_id")
    }
)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    @Column(name = "balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotBlank
    @Size(min = 3, max = 3)
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Wallet() {
    }

    public Wallet(UUID id, String userId, BigDecimal balance, String currency, Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.currency = currency;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id);
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
        private String userId;
        private BigDecimal balance = BigDecimal.ZERO;
        private String currency;
        private Long version;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Wallet build() {
            return new Wallet(id, userId, balance, currency, version, createdAt, updatedAt);
        }
    }
}
