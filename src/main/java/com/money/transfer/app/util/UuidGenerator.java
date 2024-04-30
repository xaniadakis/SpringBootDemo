package com.money.transfer.app.util;

import com.money.transfer.app.entity.Account;
import com.money.transfer.app.entity.Transaction;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidGenerator {

    /**
     * Used by the H2 database system, as defined in the correspondent liquibase script,
     * to compute the id of the {@link Account} and {@link Transaction} tables.
     * Generates and returns a new random UUID string.
     *
     * @return a unique UUID string.
     */
    public static String generate() {
        return java.util.UUID.randomUUID().toString();
    }
}
