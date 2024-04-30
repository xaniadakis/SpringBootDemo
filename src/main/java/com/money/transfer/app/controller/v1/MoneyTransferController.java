package com.money.transfer.app.controller.v1;

import com.money.transfer.app.dto.TransferRequestBody;
import com.money.transfer.app.dto.TransferResponseBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller interface defining the money transfer operations.
 *
 * API versioning is implemented to manage changes and maintain backward compatibility,
 * allowing for future updates without affecting existing integrations.
 */
@RequestMapping("/api/v1")
public interface MoneyTransferController {

    /**
     * Processes POST requests for transferring money between accounts.
     *
     * The endpoint consumes and produces JSON data.
     *
     * @param transferRequestBody the necessary data, not null.
     * @return a {@link TransferResponseBody} encapsulated in a {@link ResponseEntity}, never null.
     */
    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TransferResponseBody> transfer(@RequestBody TransferRequestBody transferRequestBody);
}
