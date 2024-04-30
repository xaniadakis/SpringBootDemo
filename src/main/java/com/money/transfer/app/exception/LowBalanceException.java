package com.money.transfer.app.exception;

import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.entity.Account;

import static com.money.transfer.app.util.constants.ExceptionConstants.LOW_BALANCE_EXCEPTION_MESSAGE;

/**
 * Custom exception thrown when an attempted transaction exceeds the available balance in the source account.
 */
public class LowBalanceException extends RuntimeException {

    public LowBalanceException(TransferRequestBody requestBody, Account sourceAccount) {
        super(String.format(LOW_BALANCE_EXCEPTION_MESSAGE, requestBody.getAmount(),
                requestBody.getCurrency(), sourceAccount.getBalance(), sourceAccount.getCurrency()));
    }
}
