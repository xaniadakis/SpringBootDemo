package com.money.transfer.app.exception;

import static com.money.transfer.app.util.constants.ExceptionConstants.SAME_ACCOUNT_EXCEPTION_MESSAGE;

/**
 * Custom exception thrown when an attempt is made to perform a transaction between the same account.
 */
public class SameAccountException extends RuntimeException {

    public SameAccountException() {
        super(SAME_ACCOUNT_EXCEPTION_MESSAGE);
    }

}
