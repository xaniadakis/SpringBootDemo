package com.money.transfer.app.controller.v1;

import com.money.transfer.app.annotation.LogRequestCourse;
import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.dto.TransferResponseBody;
import com.money.transfer.app.service.MoneyTransferService;
import com.money.transfer.app.service.MoneyTransferServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;


/**
 * Implementation of the {@link MoneyTransferController}.
 * This controller defines the endpoint handling logic for transferring money as specified in the API.
 * <p>
 * Utilizes {@link MoneyTransferServiceImpl} to perform the core business logic associated with money transfers.
 */
@Controller
@LogRequestCourse
@AllArgsConstructor
public class MoneyTransferControllerImpl implements MoneyTransferController {

    private final MoneyTransferService moneyTransferService;

    public ResponseEntity<TransferResponseBody> transfer(TransferRequestBody transferRequestBody) {
        return ResponseEntity.ok(this.moneyTransferService.transfer(transferRequestBody));
    }
}
