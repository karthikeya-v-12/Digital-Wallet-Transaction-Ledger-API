# Digital Wallet & Transaction Ledger API

A high-concurrency, double-entry inspired **Digital Wallet and Transaction Ledger API** built with **Java 21**, **Spring Boot 3.3.x**, **Spring Data JPA**, and **PostgreSQL**.

---

## 🏗️ Architecture & Core Design Decisions

1. **Optimistic Locking (`@Version`):**
   - The `Wallet` entity incorporates a `@Version` field (`version: Long`).
   - Prevents the **Double-Spending Problem** under high concurrency without holding blocking row-level database locks.
   - On concurrent modification, Hibernate detects the stale version and triggers an `OptimisticLockingFailureException`, converted by `GlobalExceptionHandler` to a `409 Conflict`.

2. **Immutable Append-Only Ledger:**
   - The `LedgerTransaction` table is append-only.
   - All columns are marked `updatable = false` to guarantee an unalterable audit trail.

3. **Strict Idempotency:**
   - Every transaction payload requires an `idempotencyKey`.
   - Re-submitting the same key returns the originally processed transaction result instead of applying duplicate balance updates.

4. **Financial Arithmetic Precision:**
   - All balances and amounts are stored using `BigDecimal` with database mapping `precision = 19, scale = 4` (accounting for fractional sub-cents and avoiding IEEE 754 floating-point errors).

5. **Resource Management:**
   - `spring.jpa.open-in-view: false` prevents long-lived database connections spanning HTTP serialization.
   - HikariCP pool tuned with explicit connection timeouts, leak detection thresholds, and idle limits.

---

## 📁 Project Structure

```
├── docker-compose.yml          # PostgreSQL 16 container setup
├── pom.xml                     # Maven project & dependencies
├── src
│   ├── main
│   │   ├── java/com/wallet/ledger
│   │   │   ├── WalletLedgerApplication.java
│   │   │   ├── controller
│   │   │   │   ├── WalletController.java
│   │   │   │   └── TransactionController.java
│   │   │   ├── dto
│   │   │   │   ├── ApiErrorResponse.java
│   │   │   │   ├── CreateWalletRequest.java
│   │   │   │   ├── TransactionRequest.java
│   │   │   │   ├── TransactionResponse.java
│   │   │   │   └── WalletResponse.java
│   │   │   ├── exception
│   │   │   │   ├── DuplicateIdempotencyKeyException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InsufficientBalanceException.java
│   │   │   │   ├── InvalidTransactionException.java
│   │   │   │   └── WalletNotFoundException.java
│   │   │   ├── model
│   │   │   │   ├── LedgerTransaction.java
│   │   │   │   ├── TransactionStatus.java
│   │   │   │   ├── TransactionType.java
│   │   │   │   └── Wallet.java
│   │   │   ├── repository
│   │   │   │   ├── LedgerTransactionRepository.java
│   │   │   │   └── WalletRepository.java
│   │   │   └── service
│   │   │       ├── TransactionService.java
│   │   │       └── WalletService.java
│   │   └── resources
│   │       └── application.yml
│   └── test
│       ├── java/com/wallet/ledger
│       │   ├── TransactionServiceTest.java
│       │   └── WalletLedgerApplicationTests.java
│       └── resources
│           └── application-test.yml
```

---

## 🚀 Getting Started

### 1. Start PostgreSQL with Docker Compose

```bash
docker compose up -d
```

Verify the container is healthy:
```bash
docker ps
```

### 2. Build & Run the Application

Using Maven:
```bash
# Run tests
mvn clean test

# Run application locally
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

---

## 📡 API Reference & cURL Examples

### 1. Create a Wallet

`POST /api/v1/wallets`

```bash
curl -X POST http://localhost:8080/api/v1/wallets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "usr_alice",
    "currency": "USD",
    "initialBalance": 500.0000
  }'
```

**Response (201 Created):**
```json
{
  "id": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
  "userId": "usr_alice",
  "balance": 500.0000,
  "currency": "USD",
  "version": 0,
  "createdAt": "2026-11-10T12:30:00Z",
  "updatedAt": "2026-11-10T12:30:00Z"
}
```

---

### 2. Execute a Transfer (Alice -> Bob)

`POST /api/v1/transactions`

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "idempotencyKey": "order-12345-transfer",
    "sourceWalletId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
    "targetWalletId": "f7e6d5c4-b3a2-1a0f-9e8d-7c6b5a4f3e2d",
    "amount": 75.5000,
    "type": "TRANSFER"
  }'
```

**Response (201 Created):**
```json
{
  "id": "e4d3c2b1-a0f9-8e7d-6c5b-4a3f2e1d0c9b",
  "transactionRef": "TXN-8473919b-c40d-45bb-b5bf-7484df24a211",
  "idempotencyKey": "order-12345-transfer",
  "sourceWalletId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
  "targetWalletId": "f7e6d5c4-b3a2-1a0f-9e8d-7c6b5a4f3e2d",
  "amount": 75.5000,
  "type": "TRANSFER",
  "status": "SUCCESS",
  "timestamp": "2026-11-10T12:35:00Z"
}
```

---

### 3. Credit / Deposit into Wallet

`POST /api/v1/transactions`

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "idempotencyKey": "deposit-ref-999",
    "targetWalletId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
    "amount": 100.0000,
    "type": "CREDIT"
  }'
```

---

### 4. Debit / Withdraw from Wallet

`POST /api/v1/transactions`

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "idempotencyKey": "atm-withdrawal-444",
    "sourceWalletId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
    "amount": 50.0000,
    "type": "DEBIT"
  }'
```

---

### 5. Query Wallet Balance

`GET /api/v1/wallets/{id}`

```bash
curl -X GET http://localhost:8080/api/v1/wallets/a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d
```

---

### 6. Query Ledger Transactions for a Wallet

`GET /api/v1/transactions/wallet/{walletId}`

```bash
curl -X GET http://localhost:8080/api/v1/transactions/wallet/a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d
```
