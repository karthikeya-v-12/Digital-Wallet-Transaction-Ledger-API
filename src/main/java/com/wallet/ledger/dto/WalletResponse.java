package com.wallet.ledger.dto;

import com.wallet.ledger.model.Wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class WalletResponse {

    private UUID id;
    private String userId;
    private BigDecimal balance;
    private String currency;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public WalletResponse() {
    }

    public WalletResponse(UUID id, String userId, BigDecimal balance, String currency, Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WalletResponse fromEntity(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .version(wallet.getVersion())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String userId;
        private BigDecimal balance;
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

        public WalletResponse build() {
            return new WalletResponse(id, userId, balance, currency, version, createdAt, updatedAt);
        }
    }
}
