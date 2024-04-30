package com.money.transfer.app.service;

import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.entity.Account;
import com.money.transfer.app.entity.Transaction;
import com.money.transfer.app.repository.AccountRepository;
import com.money.transfer.app.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for persisting a transaction as a whole.
 */
@Service
@AllArgsConstructor
public class TransactionalServiceImpl implements TransactionalService{

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    /**
     * Processes a transaction by updating balances of the source and target accounts and recording
     * the transaction details. This method is {@link Transactional}, meaning all operations either complete
     * successfully together or fail together without any partial updates being committed to the database.
     *
     * @param sourceAccount          The account from which money are withdrawn.
     * @param targetAccount          The account to which money are deposited.
     * @param amountInSourceCurrency The amount of money to withdraw from the source account in its currency.
     * @param amountInTargetCurrency The amount of money to deposit to the target account in its currency.
     * @param requestBody            Contains the original transfer details, is needed to persist a meaningful transaction log.
     */
    @Transactional
    public void processTransaction(Account sourceAccount, Account targetAccount,
                                   float amountInSourceCurrency, float amountInTargetCurrency,
                                   TransferRequestBody requestBody) {
        sourceAccount.setBalance(sourceAccount.getBalance() - amountInSourceCurrency);
        targetAccount.setBalance(targetAccount.getBalance() + amountInTargetCurrency);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        transactionRepository.save(Transaction.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .amount(requestBody.getAmount())
                .currency(requestBody.getCurrency())
                .orderedAt(LocalDateTime.now())
                .build());
    }

}
