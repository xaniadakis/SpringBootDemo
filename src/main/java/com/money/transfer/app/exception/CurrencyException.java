package com.money.transfer.app.exception;

import static com.money.transfer.app.util.constants.ExceptionConstants.CURRENCY_EXCEPTION_MESSAGE;

/**
 * Custom exception thrown when a user requests to make a transaction with an invalid currency.
 */
public class CurrencyException extends RuntimeException {

    public CurrencyException(String currency) {
        super(String.format(CURRENCY_EXCEPTION_MESSAGE, currency));
    }
}
