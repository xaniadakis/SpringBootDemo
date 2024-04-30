package com.money.transfer.app.controller.v1

import com.money.transfer.app.MoneyTransferApplication
import com.money.transfer.app.dto.TransferRequestBody
import com.money.transfer.app.entity.Account
import com.money.transfer.app.entity.Transaction
import com.money.transfer.app.exception.*
import com.money.transfer.app.repository.AccountRepository
import com.money.transfer.app.repository.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatusCode
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification
import java.time.Duration
import java.time.LocalDateTime

import static com.money.transfer.app.util.constants.ExceptionConstants.*

/**
 * Integration test for the MoneyTransferControllerImpl using an H2 database,
 * simulating the behaviour of the application against real Postman requests.
 * These tests cover various scenarios including happy path, error handling,
 * and validation checks.
 */
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(classes = MoneyTransferApplication)
class MoneyTransferControllerImplIS extends Specification {

    private List<Account> accounts

    @Autowired
    private AccountRepository accountRepository

    @Autowired
    private TransactionRepository transactionRepository

    @Autowired
    private MoneyTransferControllerImpl moneyTransferControllerImpl

    void setup() {
        accounts = accountRepository.findAll(Pageable.ofSize(2)).toList()
        assert accounts.size() == 2
    }

    /**
     * Tests the successful money transfer between two accounts
     * and verifies afterwards that the transaction has actually
     * been persisted.
     */
    def "test happy path"() {
        given:
        def src = accounts.get(0)
        def trg = accounts.get(1)
        def request = TransferRequestBody.builder()
                .amount(1)
                .currency(src.getCurrency())
                .sourceAccountId(src.getId())
                .targetAccountId(trg.getId())
                .build()

        when:
        def response = moneyTransferControllerImpl.transfer(request)
        def optional = transactionRepository.findLatestTransactionBetweenAccounts(src.getId(), trg.getId())
        Transaction transaction = optional.orElseThrow(() -> new Exception("Transaction was not persisted."))

        then:
        noExceptionThrown()
        assert Duration.between(LocalDateTime.now(), transaction.getOrderedAt()).abs().toMinutes() <= 1
        assert transaction.getAmount() == request.getAmount()
        assert transaction.getCurrency() == request.getCurrency()
        response.getStatusCode() == HttpStatusCode.valueOf(200)
        println response.body.getResponse()
    }

    /**
     * Tests the scenario where a negative amount is attempted to be transferred, expecting an exception.
     */
    def "test transfer negative amount"() {
        given:
        def request = TransferRequestBody.builder()
                .amount(-200)
                .currency(accounts.get(0).getCurrency())
                .sourceAccountId(accounts.get(0).getId())
                .targetAccountId(accounts.get(1).getId())
                .build()

        when:
        moneyTransferControllerImpl.transfer(request)

        then:
        def e = thrown(NegativeAmountException)
        e.message == String.format(NEGATIVE_AMOUNT_EXCEPTION_MESSAGE, accounts.get(0).getCurrency())
        println e.message
    }

    /**
     * Tests transferring with an invalid currency, expecting a currency validation exception.
     */
    def "test invalid currency"() {
        given:
        def currency = "NOT_A_VALID_CURRENCY"
        def request = TransferRequestBody.builder()
                .amount(1)
                .currency(currency)
                .sourceAccountId(accounts.get(0).getId())
                .targetAccountId(accounts.get(1).getId())
                .build()

        when:
        moneyTransferControllerImpl.transfer(request)

        then:
        def e = thrown(CurrencyException)
        e.message.startsWith(String.format(CURRENCY_EXCEPTION_MESSAGE, currency))
        println e.message
    }

    /**
     * Tests the error handling when attempting to transfer to or from a non-existent account.
     */
    def "test non existent account"() {
        given:
        def id = "ACCOUNT_DOES_NOT_EXIST"
        def request = TransferRequestBody.builder()
                .amount(1)
                .currency(accounts.get(0).getCurrency())
                .sourceAccountId(id)
                .targetAccountId(accounts.get(1).getId())
                .build()

        when:
        moneyTransferControllerImpl.transfer(request)

        then:
        def e = thrown(NonExistentAccountException)
        e.message == String.format(NON_EXISTENT_ACCOUNT_EXCEPTION_MESSAGE, id)
        println e.message
    }

    /**
     * Tests transferring money to the same account, which should trigger an exception.
     */
    def "test transfer to same account"() {
        given:
        def request = TransferRequestBody.builder()
                .amount(1)
                .currency(accounts.get(0).getCurrency())
                .sourceAccountId(accounts.get(0).getId())
                .targetAccountId(accounts.get(0).getId())
                .build()

        when:
        moneyTransferControllerImpl.transfer(request)

        then:
        def e = thrown(SameAccountException)
        e.message == SAME_ACCOUNT_EXCEPTION_MESSAGE
        println e.message
    }

    /**
     * Tests the scenario where the account balance is insufficient for the transaction amount.
     */
    def "test low balance"() {
        given:
        def amount = (accounts.get(0).getBalance() + 1).floatValue()
        def currency = accounts.get(0).getCurrency()
        def request = TransferRequestBody.builder()
                .currency(currency)
                .amount(amount)
                .sourceAccountId(accounts.get(0).getId())
                .targetAccountId(accounts.get(1).getId())
                .build()

        when:
        moneyTransferControllerImpl.transfer(request)

        then:
        def e = thrown(LowBalanceException)
        e.message == String.format(LOW_BALANCE_EXCEPTION_MESSAGE,
                amount, currency, accounts.get(0).getBalance(), currency)
        println e.message
    }
}
