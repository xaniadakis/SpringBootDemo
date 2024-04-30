package com.money.transfer.app.integration

import com.money.transfer.app.dto.ExchangeRateResponse
import com.money.transfer.app.exception.WebClientException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import spock.lang.Specification

import java.nio.channels.FileLock

import static com.money.transfer.app.util.constants.ExceptionConstants.WEB_CLIENT_EXCEPTION_MESSAGE

class ExchangeRateRestClientSpec extends Specification {

    private ExchangeRateRestClient exchangeRateRestClient

    private WebClient exchangeRateWebClient

    WebClient.RequestBodyUriSpec requestBodyUriSpec
    WebClient.RequestHeadersSpec requestHeadersSpec
    WebClient.RequestBodySpec requestBodySpec
    WebClient.ResponseSpec responseSpec
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec

    def setup() {
        exchangeRateWebClient = Mock(WebClient)
        requestHeadersSpec = Mock(WebClient.RequestHeadersSpec)
        responseSpec = Mock(WebClient.ResponseSpec)
        requestHeadersUriSpec = Mock(WebClient.RequestHeadersUriSpec)
        exchangeRateRestClient = new ExchangeRateRestClient(exchangeRateWebClient)
    }

    def "test fetch exchange rates"() {
        given:
        def baseCurrency = "EUR"
        def uri = UriComponentsBuilder
                .fromUriString(baseCurrency)
                .build()
                .toUriString()
        1 * exchangeRateWebClient.get() >> requestHeadersUriSpec
        1 * requestHeadersUriSpec.uri(uri) >> requestHeadersSpec
        1 * requestHeadersSpec.retrieve() >> responseSpec
        1 * responseSpec.toEntity(ExchangeRateResponse.class) >> Mono.just(new ResponseEntity<ExchangeRateResponse>(new ExchangeRateResponse("success", Map.of("EUR", 1.450 as Float, "USD", 1.25 as Float)), HttpStatus.OK))

        when:
        def response = exchangeRateRestClient.fetchExchangeRates(baseCurrency)

        then:
        noExceptionThrown()
        response instanceof ExchangeRateResponse
        assert response.getConversion_rates().size()>0
        response.getResult() == "success"

    }


    def "test error while fetch exchange rates"() {
        given:
        def baseCurrency = "EUR"
        def uri = UriComponentsBuilder
                .fromUriString(baseCurrency)
                .build()
                .toUriString()
        1 * exchangeRateWebClient.get() >> requestHeadersUriSpec
        1 * requestHeadersUriSpec.uri(uri) >> requestHeadersSpec
        1 * requestHeadersSpec.retrieve() >> responseSpec
        1 * responseSpec.toEntity(ExchangeRateResponse.class) >>
                { throw new Exception("400") }
        when:
        exchangeRateRestClient.fetchExchangeRates(baseCurrency)

        then:
        def e = thrown(WebClientException)
        e.message == WEB_CLIENT_EXCEPTION_MESSAGE

    }
}
