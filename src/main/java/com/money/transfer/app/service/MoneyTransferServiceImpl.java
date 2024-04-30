package com.money.transfer.app.service;

import com.money.transfer.app.dto.ExchangeRateResponse;
import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.dto.TransferResponseBody;
import com.money.transfer.app.entity.Account;
import com.money.transfer.app.exception.*;
import com.money.transfer.app.integration.ExchangeRateRestClient;
import com.money.transfer.app.repository.AccountRepository;
import com.money.transfer.app.repository.CurrencyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * Service responsible to perform the core business logic.
 * That is, executing money transfers between accounts, performing validations
 * and currency conversions.
 */
@Slf4j
@Service
@AllArgsConstructor
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private final AccountRepository accountRepository;

    private final CurrencyRepository currencyRepository;

    private final TransactionalService transactionalService;

    private final ExchangeRateRestClient exchangeRateRestClient;

    /**
     * Performs a transfer between two accounts after validating conditions and converting currency if necessary.
     *
     * @param requestBody the transfer request.
     * @return {@link TransferResponseBody}
     * @throws CurrencyException           if the specified currency is not supported.
     * @throws NegativeAmountException     if the amount specified to transact is negative.
     * @throws SameAccountException        if the source and target accounts are the same.
     * @throws NonExistentAccountException if an account does not exist.
     * @throws LowBalanceException         if the source account does not have enough balance to support the requested transaction.
     */
    public TransferResponseBody transfer(TransferRequestBody requestBody) {

        validateTransferRequest(requestBody);

        Account sourceAccount = accountRepository.findById(requestBody.getSourceAccountId())
                .orElseThrow(() -> new NonExistentAccountException(requestBody.getSourceAccountId()));

        Account targetAccount = accountRepository.findById(requestBody.getTargetAccountId())
                .orElseThrow(() -> new NonExistentAccountException(requestBody.getTargetAccountId()));

        float amountInSourceCurrency = calculateAmountInAccountCurrency(
                requestBody.getAmount(), requestBody.getCurrency(), sourceAccount.getCurrency());
        float amountInTargetCurrency = calculateAmountInAccountCurrency(
                requestBody.getAmount(), requestBody.getCurrency(), targetAccount.getCurrency());

        checkBalance(requestBody, sourceAccount, amountInSourceCurrency);

        transactionalService.processTransaction(sourceAccount, targetAccount,
                amountInSourceCurrency, amountInTargetCurrency,
                requestBody);

        return createResponse(requestBody);
    }

    /**
     * Validates the transfer request by checking for negative amounts, identical account IDs,
     * and unsupported currencies, before processing.
     *
     * @param requestBody the transfer request details.
     * @throws NegativeAmountException if the amount specified in the request is negative.
     * @throws SameAccountException    if the source and target account IDs are identical.
     * @throws CurrencyException       if the specified currency is invalid.
     */
    private void validateTransferRequest(TransferRequestBody requestBody) {

        if (requestBody.getAmount() < 0) {
            throw new NegativeAmountException(requestBody.getCurrency());
        }

        if (requestBody.getSourceAccountId().trim().equals(requestBody.getTargetAccountId().trim())) {
            throw new SameAccountException();
        }

        if (currencyRepository.findById(requestBody.getCurrency()).isEmpty()) {
            throw new CurrencyException(requestBody.getCurrency());
        }
    }


    /**
     * Ensures the source account has sufficient balance for the transfer.
     *
     * @param sourceAccount          The account from which money will be withdrawn.
     * @param amountInSourceCurrency The amount to be withdrawn, converted to the account's currency if necessary.
     */
    private static void checkBalance(TransferRequestBody requestBody, Account sourceAccount, float amountInSourceCurrency) {
        if (sourceAccount.getBalance() < amountInSourceCurrency)
            throw new LowBalanceException(requestBody, sourceAccount);
    }

    /**
     * Converts the transfer amount to the target account's currency if necessary.
     *
     * @param amount              The original amount to be transferred.
     * @param transactionCurrency The currency of the transaction.
     * @param accountCurrency     The currency of the account.
     * @return The converted amount or the original amount if no conversion is needed.
     */
    private float calculateAmountInAccountCurrency(float amount, String transactionCurrency, String accountCurrency) {
        if (transactionCurrency.equalsIgnoreCase(accountCurrency)) {
            return amount;
        } else {
            return convertCurrency(amount, transactionCurrency, accountCurrency);
        }
    }

    /**
     * Converts a given amount from one currency to another.
     *
     * @param oldAmount   The amount to convert.
     * @param oldCurrency The currency of the old amount.
     * @param newCurrency The target currency.
     * @return The amount converted to the new currency.
     */
    private float convertCurrency(float oldAmount, String oldCurrency, String newCurrency) {
        ExchangeRateResponse response = exchangeRateRestClient.fetchExchangeRates(oldCurrency);
        float newAmount = oldAmount * response.conversion_rates.get(newCurrency);
        log.debug("Currency converted: {} {} to {} {}", oldAmount, oldCurrency, newAmount, newCurrency);
        return newAmount;
    }

    /**
     * Constructs a success response for a completed money transfer.
     *
     * @param requestBody contains the details of the transfer request.
     * @return A response summarizing the successful transaction.
     */
    private static TransferResponseBody createResponse(TransferRequestBody requestBody) {
        return TransferResponseBody.builder()
                .response(String.format("Transfer of %.2f %s from account %s to account %s completed successfully.",
                        requestBody.getAmount(),
                        requestBody.getCurrency(),
                        requestBody.getSourceAccountId(),
                        requestBody.getTargetAccountId()))
                .build();
    }
}
