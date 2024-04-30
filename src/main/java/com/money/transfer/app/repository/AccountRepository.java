package com.money.transfer.app.repository;

import com.money.transfer.app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the {@link Account} entity, extending {@link JpaRepository} for basic CRUD operations.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

}
