package com.money.transfer.app.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.money.transfer.app.dto.TransferResponseBody;
import com.money.transfer.app.repository.CurrencyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Provides global exception handling for the application.
 * <p>
 * This class is marked with {@link ControllerAdvice} to apply to all controllers,
 * in order to centralize exception handling and reduce repetitive
 * error handling code inside the controllers.
 */
@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final CurrencyRepository currencyRepository;

    /**
     * Handles general {@link RuntimeException}s that may occur during the processing of a request.
     * It also handles all the classes that extend {@link RuntimeException} and are not handled by any
     * method below, such as {@link LowBalanceException}, {@link NegativeAmountException} etc.
     * Returns a {@link ResponseEntity} with the error message and a 400 status.
     *
     * @param e the runtime exception that was thrown
     * @return a {@link ResponseEntity} containing the error message and HTTP status
     */
    @ExceptionHandler({RuntimeException.class})
    ResponseEntity<TransferResponseBody> handle(RuntimeException e) {
        return new ResponseEntity<>(TransferResponseBody.builder().response(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions of type {@link WebClientException}, which can be thrown in case the integration with the
     * exchange rate service fails.
     * Returns a {@link ResponseEntity} with the error message and a 500 status.
     *
     * @param e the WebClientException that was thrown
     * @return a {@link ResponseEntity} containing the error message and HTTP status
     */
    @ExceptionHandler(WebClientException.class)
    ResponseEntity<TransferResponseBody> handle(WebClientException e) {
        return new ResponseEntity<>(TransferResponseBody.builder().response(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@link CurrencyException}, which occur when a user requests to initiate a transaction with
     * an invalid currency.
     * Returns a detailed JSON response, with all the acceptable currencies
     * that a user is able to utilize, and a 500 status.
     * This method uses {@link Gson} to serialize the currency data into JSON format and pretty print
     * it to be more readable.
     *
     * @param e the CurrencyException that was thrown
     * @return a {@link ResponseEntity} containing the error message and serialized currency details
     */
    @ExceptionHandler(CurrencyException.class)
    ResponseEntity<String> handle(CurrencyException e) {
        return new ResponseEntity<>(e.getMessage() +
                new GsonBuilder().setPrettyPrinting().create().toJson(currencyRepository.findAll())
                , HttpStatus.BAD_REQUEST);
    }
}
