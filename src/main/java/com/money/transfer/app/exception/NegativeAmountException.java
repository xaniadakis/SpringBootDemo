package com.money.transfer.app.exception;

import static com.money.transfer.app.util.constants.ExceptionConstants.NEGATIVE_AMOUNT_EXCEPTION_MESSAGE;

/**
 * Custom exception thrown when a transaction request includes a negative amount.
 */
public class NegativeAmountException extends RuntimeException {


    public NegativeAmountException(String currency) {
        super(String.format(NEGATIVE_AMOUNT_EXCEPTION_MESSAGE, currency));
    }
}
