package com.money.transfer.app.service;


import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.dto.TransferResponseBody;

public interface MoneyTransferService {

    TransferResponseBody transfer(TransferRequestBody requestBody);

}
