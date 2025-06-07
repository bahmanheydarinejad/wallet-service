package com.company.walletservice.models.repositories;

import com.company.walletservice.models.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<WalletTransaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM WalletTransaction t WHERE t.userId = :userId")
    BigDecimal getUserBalance(Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM WalletTransaction t")
    BigDecimal getTotalTransactionsAmount();

}
