package com.money.transfer.app.exception;

import static com.money.transfer.app.util.constants.ExceptionConstants.NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE;

/**
 * Custom exception thrown when a request is referring to an account that does not exist in the system.
 */
public class NonExistentAccountException extends RuntimeException {

    public NonExistentAccountException(String accountId) {
        super(String.format(NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE, accountId));
    }
}
