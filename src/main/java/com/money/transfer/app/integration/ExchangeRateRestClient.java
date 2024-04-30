package com.money.transfer.app.integration;

import com.money.transfer.app.dto.ExchangeRateResponse;
import com.money.transfer.app.exception.WebClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Rest Client to integrate with the external exchange rate API.
 * This component uses a {@link WebClient} to make HTTP requests to retrieve exchange rates
 * in order to help its caller service to accurately convert between currencies.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateRestClient {

    private final WebClient exchangeRateWebClient;

    /**
     * Fetches exchange rate data for a given base currency.
     *
     * This method constructs a URL for the exchange rate API using the provided base currency
     * and sends a GET request through the {@link WebClient}.
     *
     * @param baseCurrency The base currency code (e.g., "USD") used to fetch corresponding exchange rates.
     * @return {@link ExchangeRateResponse} containing exchange rate data
     * @throws WebClientException in case there is an error during the web client operation
     */
    public ExchangeRateResponse fetchExchangeRates(String baseCurrency) {
        try {
            final String url = UriComponentsBuilder.fromUriString(baseCurrency)
                    .build()
                    .toUriString();
            ResponseEntity<ExchangeRateResponse> responseEntity = exchangeRateWebClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(ExchangeRateResponse.class)
                    .block();
            log.debug("ExchangeRate API responded: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch (Exception e) {
            throw new WebClientException();
        }
    }

}
