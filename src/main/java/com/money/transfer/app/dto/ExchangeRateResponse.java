package com.money.transfer.app.dto;

import com.money.transfer.app.integration.ExchangeRateRestClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents the response structure retrieved from the external API with {@link ExchangeRateRestClient}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateResponse{

    public String result;

    /**
     * A mapping of currency codes to their corresponding exchange rates.
     */
    public Map<String, Float> conversion_rates;

}
