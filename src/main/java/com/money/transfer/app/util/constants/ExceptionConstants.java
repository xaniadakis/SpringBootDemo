package com.money.transfer.app.util.constants;

/**
 * Defines constant messages for exceptions used in the application.
 */
public class ExceptionConstants {

    public static final String CURRENCY_EXCEPTION_MESSAGE = "%s is not a valid currency.\nAcceptable currencies are listed below:\n";
    public static final String LOW_BALANCE_EXCEPTION_MESSAGE = "Cannot proceed to money transfer due to low balance. " +
            "Unable to transfer %.2f %s with a balance of %.2f %s.";
    public static final String NEGATIVE_AMOUNT_EXCEPTION_MESSAGE = "Cannot transfer negative amount of %s.";
    public static final String NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE = "Account with ID: %s is non existent.";
    public static final String SAME_ACCOUNT_EXCEPTION_MESSAGE = "Source and target account cannot be the same.";
    public static final String WEB_CLIENT_GENERAL_EXCEPTION_MESSAGE = "Error while fetching exchange rates from API.";

    public static final String EXTERNAL_SERVER_EXCEPTION_MESSAGE = "Failed to retrieve exchange rates due to external server error.";

    public static final String INTERNAL_SERVER_EXCEPTION_MESSAGE = "Failed to send request to the exchange rate service.";
    public static final String REQUEST_TIME_OUT_EXCEPTION_MESSAGE = "Request to the exchange rate service timed out.";
}
