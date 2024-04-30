package com.money.transfer.app.repository;

import com.money.transfer.app.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the {@link Currency} entity, extending {@link JpaRepository} for basic CRUD operations.
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

}
