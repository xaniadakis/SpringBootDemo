package com.money.transfer.app.exception;


/**
 * Exception thrown when an error occurs while the client tries to integrate
 * with the exchange rate service, to retrieve information to accurately convert
 * between currencies.
 */
public class WebClientException extends RuntimeException {

    public WebClientException(String message) {
        super(message);
    }

}
