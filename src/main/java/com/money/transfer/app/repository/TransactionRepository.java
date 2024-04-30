package com.money.transfer.app.repository;

import com.money.transfer.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for the {@link Transaction} entity, extending {@link JpaRepository} for basic CRUD operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount.id = :sourceAccountId AND t.targetAccount.id = :targetAccountId) " +
            "ORDER BY t.orderedAt DESC")
    Optional<Transaction> findLatestTransactionBetweenAccounts(String sourceAccountId, String targetAccountId);


}
