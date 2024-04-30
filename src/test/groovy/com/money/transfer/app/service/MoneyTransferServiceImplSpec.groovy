package com.money.transfer.app.service

import com.money.transfer.app.dto.ExchangeRateResponse
import com.money.transfer.app.dto.TransferRequestBody
import com.money.transfer.app.dto.TransferResponseBody
import com.money.transfer.app.entity.Account
import com.money.transfer.app.entity.Currency
import com.money.transfer.app.exception.*
import com.money.transfer.app.integration.ExchangeRateRestClient
import com.money.transfer.app.repository.AccountRepository
import com.money.transfer.app.repository.CurrencyRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static com.money.transfer.app.util.constants.ExceptionConstants.*

/**
 * Unit test for the MoneyTransferService, in which lies the core business logic.
 */
class MoneyTransferServiceImplSpec extends Specification {

    private AccountRepository accountRepository

    private CurrencyRepository currencyRepository

    private TransactionalServiceImpl transactionService

    private ExchangeRateRestClient exchangeRateRestClient

    private MoneyTransferServiceImpl moneyTransferService

    def setup() {
        accountRepository = Mock(AccountRepository)
        currencyRepository = Mock(CurrencyRepository)
        transactionService = Mock(TransactionalServiceImpl)
        exchangeRateRestClient = Mock(ExchangeRateRestClient)
        moneyTransferService = new MoneyTransferServiceImpl(accountRepository, currencyRepository, transactionService, exchangeRateRestClient)
    }

    /**
     * Verifies that money transfer between two different accounts with the same currency executes correctly
     * without throwing any exceptions and produces the expected response.
     */
    def "test happy path same currency"() {
        given:
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency("EUR")
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("EUR", "Euro", "Eurozone"))
        accountRepository.findById(requestBody.getSourceAccountId()) >>
                Optional.of(new Account("sourceAccountId", 1000, "EUR", LocalDateTime.now().minusYears(1)))
        accountRepository.findById(requestBody.getTargetAccountId()) >>
                Optional.of(new Account("targetAccountId", 100, "EUR", LocalDateTime.now().minusMonths(2)))

        when:
        def responseBody = moneyTransferService.transfer(requestBody)

        then:
        noExceptionThrown()
        responseBody instanceof TransferResponseBody
    }

    /**
     * Tests the handling of a money transfer when the transaction currency
     * is different from the source account's currency.
     */
    def "test happy path transaction currency different than source account"() {
        given:
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency("USD")
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("USD", "United States Dollar", "United States"))
        accountRepository.findById(requestBody.getSourceAccountId()) >>
                Optional.of(new Account("sourceAccountId", 1000, "EUR", LocalDateTime.now().minusYears(1)))
        accountRepository.findById(requestBody.getTargetAccountId()) >>
                Optional.of(new Account("targetAccountId", 100, "USD", LocalDateTime.now().minusMonths(2)))
        exchangeRateRestClient.fetchExchangeRates("USD") >>
                ExchangeRateResponse.builder()
                        .result("success")
                        .conversion_rates(Map.of("USD", 1.0 as Float,
                                "ETB", 57.3679 as Float,
                                "EUR", 0.9341 as Float,
                                "FJD", 2.2583 as Float,
                                "FOK", 6.9689 as Float))
                        .build()

        when:
        def responseBody = moneyTransferService.transfer(requestBody)

        then:
        noExceptionThrown()
        responseBody instanceof TransferResponseBody
    }

    /**
     * Tests the handling of a money transfer when the transaction currency
     * is different from the target account's currency.
     */
    def "test happy path transaction currency different than target account"() {
        given:
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency("EUR")
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("EUR", "Euro", "Eurozone"))
        accountRepository.findById(requestBody.getSourceAccountId()) >>
                Optional.of(new Account("sourceAccountId", 1000, "EUR", LocalDateTime.now().minusYears(1)))
        accountRepository.findById(requestBody.getTargetAccountId()) >>
                Optional.of(new Account("targetAccountId", 100, "USD", LocalDateTime.now().minusMonths(2)))
        exchangeRateRestClient.fetchExchangeRates("EUR") >>
                ExchangeRateResponse.builder()
                        .result("success")
                        .conversion_rates(Map.of("EUR", 1.0 as Float,
                                "AED", 3.9314 as Float,
                                "AFN", 77.2707 as Float,
                                "AMD", 416.3709 as Float,
                                "USD", 1.0706 as Float))
                        .build()

        when:
        def responseBody = moneyTransferService.transfer(requestBody)

        then:
        noExceptionThrown()
        responseBody instanceof TransferResponseBody
    }

    /**
     * Checks for proper error handling when attempting to transfer money to the same account,
     * expecting a SameAccountException to be thrown.
     */
    def "test same account error"() {
        given:
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency("EUR")
                .sourceAccountId("sourceAccountId")
                .targetAccountId("sourceAccountId")
                .build()

        when:
        moneyTransferService.transfer(requestBody)

        then:
        def e = thrown(SameAccountException)
        e.message == SAME_ACCOUNT_EXCEPTION_MESSAGE
        println e.message
    }

    /**
     * Tests the system's response to an invalid currency during a transaction,
     * expecting a CurrencyException for unsupported currency.
     */
    def "test invalid transaction currency"() {
        given:
        def currency = "ZIMBABWE_CURRENCY"
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency(currency)
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >> Optional.empty()

        when:
        moneyTransferService.transfer(requestBody)

        then:
        def e = thrown(CurrencyException)
        e.message.startsWith(String.format(CURRENCY_EXCEPTION_MESSAGE, currency))
        println e.message
    }

    /**
     * Verifies behavior when a negative amount of money is specified for a transfer,
     * expecting a NegativeAmountException to be indicate the error.
     */
    def "test transfer negative amount of money"() {
        given:
        def requestBody = TransferRequestBody.builder()
                .amount(-1000)
                .currency("EUR")
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("EUR", "Euro", "Eurozone"))
        accountRepository.findById(requestBody.getSourceAccountId()) >>
                Optional.of(new Account("sourceAccountId", 1000, "EUR", LocalDateTime.now().minusYears(1)))
        accountRepository.findById(requestBody.getTargetAccountId()) >>
                Optional.of(new Account("targetAccountId", 100, "EUR", LocalDateTime.now().minusMonths(2)))

        when:
        moneyTransferService.transfer(requestBody)

        then:
        def e = thrown(NegativeAmountException)
        e.message == String.format(NEGATIVE_AMOUNT_EXCEPTION_MESSAGE, "EUR")
        println e.message
    }

    /**
     * Tests the application's response to transaction requests that would exceed the source account's balance,
     * expecting a LowBalanceException to be thrown.
     */
    def "test low balance error"() {
        given:
        def sourceAccount = new Account("sourceAccountId", 1000, "EUR", LocalDateTime.now().minusYears(1))
        def targetAccount = new Account("targetAccountId", 100, "EUR", LocalDateTime.now().minusMonths(2))
        float amount = (sourceAccount.getBalance() + 1).floatValue()
        def currency = sourceAccount.getCurrency()
        def requestBody = TransferRequestBody.builder()
                .amount(amount)
                .currency("EUR")
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("EUR", "Euro", "Eurozone"))
        accountRepository.findById(requestBody.getSourceAccountId()) >> Optional.of(sourceAccount)
        accountRepository.findById(requestBody.getTargetAccountId()) >> Optional.of(targetAccount)

        when:
        moneyTransferService.transfer(requestBody)

        then:
        def e = thrown(LowBalanceException)
        e.message == String.format(LOW_BALANCE_EXCEPTION_MESSAGE,
                amount, currency, sourceAccount.getBalance(), currency)
        println e.message
    }

    /**
     * Simulates a transfer request involving a non-existent source account expecting a NonExistentAccountException.
     */
    def "test non existent source account"() {
        given:
        def nonExistentId = "sourceAccountId"
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency("EUR")
                .sourceAccountId(nonExistentId)
                .targetAccountId("targetAccountId")
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("EUR", "Euro", "Eurozone"))
        accountRepository.findById(requestBody.getSourceAccountId()) >>
                Optional.empty()
        accountRepository.findById(requestBody.getTargetAccountId()) >>
                Optional.of(new Account("targetAccountId", 100, "EUR", LocalDateTime.now().minusMonths(2)))

        when:
        moneyTransferService.transfer(requestBody)

        then:
        def e = thrown(NonExistentAccountException)
        e.message == String.format(NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE, nonExistentId)
        println e.message
    }

    /**
     * Simulates a transfer request involving a non-existent target account expecting a NonExistentAccountException.
     */
    def "test non existent target account"() {
        given:
        def nonExistentId = "targetAccountId"
        def requestBody = TransferRequestBody.builder()
                .amount(1)
                .currency("EUR")
                .sourceAccountId("sourceAccountId")
                .targetAccountId(nonExistentId)
                .build()
        currencyRepository.findById(requestBody.getCurrency()) >>
                Optional.of(new Currency("EUR", "Euro", "Eurozone"))
        accountRepository.findById(requestBody.getSourceAccountId()) >>
                Optional.of(new Account("sourceAccountId", 1000, "EUR", LocalDateTime.now().minusYears(1)))
        accountRepository.findById(requestBody.getTargetAccountId()) >>
                Optional.empty()

        when:
        moneyTransferService.transfer(requestBody)

        then:
        def e = thrown(NonExistentAccountException)
        e.message == String.format(NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE, nonExistentId)
        println e.message
    }
}
