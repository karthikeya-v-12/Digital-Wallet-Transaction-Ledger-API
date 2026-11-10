package com.wallet.ledger.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateWalletRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotNull(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
    private String currency;

    @DecimalMin(value = "0.00", message = "initialBalance cannot be negative")
    private BigDecimal initialBalance;

    public CreateWalletRequest() {
    }

    public CreateWalletRequest(String userId, String currency, BigDecimal initialBalance) {
        this.userId = userId;
        this.currency = currency;
        this.initialBalance = initialBalance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String currency;
        private BigDecimal initialBalance;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder initialBalance(BigDecimal initialBalance) {
            this.initialBalance = initialBalance;
            return this;
        }

        public CreateWalletRequest build() {
            return new CreateWalletRequest(userId, currency, initialBalance);
        }
    }
}
