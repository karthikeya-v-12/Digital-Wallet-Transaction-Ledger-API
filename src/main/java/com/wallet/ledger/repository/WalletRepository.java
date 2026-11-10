package com.wallet.ledger.repository;

import com.wallet.ledger.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    
    List<Wallet> findByUserId(String userId);

    Optional<Wallet> findByUserIdAndCurrency(String userId, String currency);
}
