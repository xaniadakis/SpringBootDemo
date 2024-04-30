package com.money.transfer.app.configuration;

import com.money.transfer.app.integration.ExchangeRateRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Configuration class for setting up application-specific beans and configurations.
 * This class specifically configures the {@link WebClient} used for making HTTP requests
 * to external services, such as the exchange rate API.
 */
@Configuration
public class AppConfiguration {

    /**
     * The URL of the exchange rate service, injected from application properties.
     */
    @Value("${exchange.rate.api.url}")
    private String serviceUrl;

    /**
     * Creates a {@link WebClient} bean configured with the base URL for the exchange rate API.
     * This client is used in {@link ExchangeRateRestClient} to make web requests to the exchange rate API.
     * @return the configured {@link WebClient} instance
     */
    @Bean
    WebClient exchangeRateWebClient() {
        return WebClient.builder().baseUrl(serviceUrl).build();
    }
}
