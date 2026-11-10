package com.wallet.ledger.repository;

import com.wallet.ledger.model.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, UUID> {

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<LedgerTransaction> findByIdempotencyKey(String idempotencyKey);

    Optional<LedgerTransaction> findByTransactionRef(String transactionRef);

    List<LedgerTransaction> findBySourceWalletIdOrTargetWalletIdOrderByTimestampDesc(UUID sourceWalletId, UUID targetWalletId);
}
