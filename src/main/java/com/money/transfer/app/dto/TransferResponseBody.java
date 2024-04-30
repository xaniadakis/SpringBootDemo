package com.money.transfer.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object representing the response body of a money transfer operation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseBody {

    private String response;
}
