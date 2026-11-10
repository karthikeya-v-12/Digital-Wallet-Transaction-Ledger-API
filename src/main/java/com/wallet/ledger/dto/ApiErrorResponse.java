package com.wallet.ledger.dto;

import java.time.Instant;
import java.util.List;

public class ApiErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private Instant timestamp;
    private List<String> details;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(int status, String error, String message, String path, Instant timestamp, List<String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int status;
        private String error;
        private String message;
        private String path;
        private Instant timestamp;
        private List<String> details;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder details(List<String> details) {
            this.details = details;
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(status, error, message, path, timestamp, details);
        }
    }
}
