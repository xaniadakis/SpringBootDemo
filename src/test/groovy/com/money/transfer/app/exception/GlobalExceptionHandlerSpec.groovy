package com.money.transfer.app.exception

import com.money.transfer.app.dto.TransferRequestBody
import com.money.transfer.app.dto.TransferResponseBody
import com.money.transfer.app.entity.Account
import com.money.transfer.app.repository.CurrencyRepository
import org.springframework.http.HttpStatusCode
import spock.lang.Specification

import java.time.LocalDateTime

import static com.money.transfer.app.util.constants.ExceptionConstants.*

class GlobalExceptionHandlerSpec extends Specification {

    private CurrencyRepository currencyRepository

    private GlobalExceptionHandler globalExceptionHandler

    def setup() {
        currencyRepository = Mock(CurrencyRepository)
        globalExceptionHandler = new GlobalExceptionHandler(currencyRepository)
    }

    def "handleRuntimeException"() {
        given:
        def message = "ERROR BLA"
        def exception = new RuntimeException(message)

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(400)
        response.getBody() instanceof TransferResponseBody
        response.getBody().getResponse() == message
    }

    def "handleWebClientException"() {
        given:
        def exception = new WebClientException(WEB_CLIENT_GENERAL_EXCEPTION_MESSAGE)

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(500)
        response.getBody() instanceof TransferResponseBody
    }

    def "handleCurrencyException"() {
        given:
        def exception = new CurrencyException()

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(400)
        response.getBody() instanceof String
    }

    def "handleLowBalanceException"() {
        given:
        def requestBody = TransferRequestBody.builder()
                .amount(15)
                .currency("USD")
                .build()
        def sourceAccount = new Account("id", 11, "USD", LocalDateTime.now().minusYears(1))
        def exception = new LowBalanceException(requestBody, sourceAccount)

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(400)
        response.getBody() instanceof TransferResponseBody
        response.getBody().getResponse() == String.format(LOW_BALANCE_EXCEPTION_MESSAGE,
                requestBody.getAmount(), requestBody.getCurrency(),
                sourceAccount.getBalance(), sourceAccount.getCurrency())
    }

    def "handleNegativeAmountException"() {
        given:
        def currency = "USD"
        def exception = new NegativeAmountException(currency)

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(400)
        response.getBody() instanceof TransferResponseBody
        response.getBody().getResponse() == String.format(NEGATIVE_AMOUNT_EXCEPTION_MESSAGE, currency)
    }

    def "handleNonExistentAccountException"() {
        given:
        def accountId = "accountId"
        def exception = new NonExistentAccountException(accountId)

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(400)
        response.getBody() instanceof TransferResponseBody
        response.getBody().getResponse() == String.format(NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE, accountId)
    }

    def "handleSameAccountException"() {
        given:
        def exception = new SameAccountException()

        when:
        def response = globalExceptionHandler.handle(exception)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(400)
        response.getBody() instanceof TransferResponseBody
        response.getBody().getResponse() == SAME_ACCOUNT_EXCEPTION_MESSAGE
    }
}
