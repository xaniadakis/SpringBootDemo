package com.money.transfer.app.exception;


import static com.money.transfer.app.util.constants.ExceptionConstants.WEB_CLIENT_EXCEPTION_MESSAGE;

/**
 * Exception thrown when an error occurs while the client tries to integrate
 * with the exchange rate service, to retrieve information to accurately convert
 * between currencies.
 */
public class WebClientException extends RuntimeException {

    public WebClientException() {
        super(WEB_CLIENT_EXCEPTION_MESSAGE);
    }

}
