package com.money.transfer.app.integration;

import com.money.transfer.app.dto.ExchangeRateResponse;
import com.money.transfer.app.exception.WebClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static com.money.transfer.app.util.constants.ExceptionConstants.*;

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
     * <p>
     * This method constructs a URL for the exchange rate API using the provided base currency
     * and sends a GET request through the {@link WebClient}.
     *
     * @param baseCurrency The base currency code to fetch exchange rates in relation to.
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
                    .block(Duration.ofSeconds(10));
            log.debug("ExchangeRate API responded: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch (WebClientResponseException e) {
            throw new WebClientException(EXTERNAL_SERVER_EXCEPTION_MESSAGE);
        } catch (WebClientRequestException e) {
            throw new WebClientException(INTERNAL_SERVER_EXCEPTION_MESSAGE);
        } catch (Exception e) {
            if (e.getCause() instanceof TimeoutException) {
                throw new WebClientException(REQUEST_TIME_OUT_EXCEPTION_MESSAGE);
            } else {
                throw new WebClientException(WEB_CLIENT_GENERAL_EXCEPTION_MESSAGE);
            }
        }
    }
}
