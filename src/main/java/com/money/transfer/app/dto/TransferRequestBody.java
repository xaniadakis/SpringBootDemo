package com.money.transfer.app.dto;

import lombok.*;

/**
 * Data transfer object representing the request body for initiating a money transfer.
 * This object includes all necessary details as requested in the README.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestBody {

    private String sourceAccountId;

    private String targetAccountId;

    private float amount;

    private String currency;
}
