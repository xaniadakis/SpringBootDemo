package com.money.transfer.app.service

import com.money.transfer.app.dto.TransferRequestBody
import com.money.transfer.app.entity.Account
import com.money.transfer.app.repository.AccountRepository
import com.money.transfer.app.repository.TransactionRepository
import spock.lang.Specification

import java.time.LocalDateTime

class TransactionalServiceImplSpec extends Specification {

    private AccountRepository accountRepository

    private TransactionRepository transactionRepository

    private TransactionalServiceImpl transactionService

    def setup(){
        accountRepository = Mock(AccountRepository)
        transactionRepository = Mock(TransactionRepository)
        transactionService = new TransactionalServiceImpl(accountRepository, transactionRepository)
    }

    def "test processTransaction"(){
        given:
        def sourceAccount = new Account("sourceAccountId", 500, "EUR", LocalDateTime.now().minusYears(1))
        def targetAccount = new Account("targetAccountId", 100, "EUR", LocalDateTime.now().minusMonths(2))
        def requestBody = TransferRequestBody.builder()
                .amount(10)
                .currency("EUR")
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .build()
        float amountInSourceCurrency = requestBody.getAmount()
        float amountInTargetCurrency = requestBody.getAmount()

        when:
        transactionService = transactionService.processTransaction(
                sourceAccount, targetAccount, amountInSourceCurrency, amountInTargetCurrency, requestBody)

        then:
        noExceptionThrown()
    }
}
