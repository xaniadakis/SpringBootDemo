package com.money.transfer.app.service;

import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.entity.Account;

public interface TransactionalService {

    void processTransaction(Account sourceAccount, Account targetAccount,
                            float amountInSourceCurrency, float amountInTargetCurrency,
                            TransferRequestBody requestBody);
}
