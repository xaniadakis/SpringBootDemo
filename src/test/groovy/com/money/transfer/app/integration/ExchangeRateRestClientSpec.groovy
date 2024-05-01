package com.money.transfer.app.integration

import com.money.transfer.app.dto.ExchangeRateResponse
import com.money.transfer.app.exception.WebClientException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import spock.lang.Specification

import java.nio.charset.Charset
import java.util.concurrent.TimeoutException

import static com.money.transfer.app.util.constants.ExceptionConstants.EXTERNAL_SERVER_EXCEPTION_MESSAGE
import static com.money.transfer.app.util.constants.ExceptionConstants.INTERNAL_SERVER_EXCEPTION_MESSAGE
import static com.money.transfer.app.util.constants.ExceptionConstants.REQUEST_TIME_OUT_EXCEPTION_MESSAGE
import static com.money.transfer.app.util.constants.ExceptionConstants.WEB_CLIENT_GENERAL_EXCEPTION_MESSAGE

class ExchangeRateRestClientSpec extends Specification {

    private ExchangeRateRestClient exchangeRateRestClient

    private WebClient exchangeRateWebClient

    WebClient.RequestHeadersSpec requestHeadersSpec
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
        assert response.getConversion_rates().size() > 0
        response.getResult() == "success"

    }

    def "test WebClientResponseException while fetch exchange rates"() {
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
                {
                    throw new WebClientResponseException(
                            "API endpoint not found",
                            404,
                            "Not Found",
                            new HttpHeaders(),
                            "The requested resource is not available".bytes,
                            Charset.defaultCharset()
                    )
                }
        when:
        exchangeRateRestClient.fetchExchangeRates(baseCurrency)

        then:
        def e = thrown(WebClientException)
        e.message == EXTERNAL_SERVER_EXCEPTION_MESSAGE
    }

    def "test WebClientRequestException while fetch exchange rates"() {
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
                {
                    throw new WebClientRequestException(
                            new RuntimeException("Simulated network issue"),
                            HttpMethod.GET,
                            new URI("http://malformed_uri.com"),
                            new HttpHeaders())
                }
        when:
        exchangeRateRestClient.fetchExchangeRates(baseCurrency)

        then:
        def e = thrown(WebClientException)
        e.message == INTERNAL_SERVER_EXCEPTION_MESSAGE
    }

    def "test TimeoutException while fetch exchange rates"() {
        given:
        def msg = "Operation timed out after 8 seconds"
        def baseCurrency = "EUR"
        def uri = UriComponentsBuilder
                .fromUriString(baseCurrency)
                .build()
                .toUriString()
        1 * exchangeRateWebClient.get() >> requestHeadersUriSpec
        1 * requestHeadersUriSpec.uri(uri) >> requestHeadersSpec
        1 * requestHeadersSpec.retrieve() >> responseSpec
        1 * responseSpec.toEntity(ExchangeRateResponse.class) >> { throw new IllegalStateException(msg, new TimeoutException(msg)) }

        when:
        exchangeRateRestClient.fetchExchangeRates(baseCurrency)

        then:
        def e = thrown(WebClientException)
        e.message == REQUEST_TIME_OUT_EXCEPTION_MESSAGE
    }

    def "test general exception while fetch exchange rates"() {
        given:
        def baseCurrency = "EUR"
        def uri = UriComponentsBuilder
                .fromUriString(baseCurrency)
                .build()
                .toUriString()
        1 * exchangeRateWebClient.get() >> requestHeadersUriSpec
        1 * requestHeadersUriSpec.uri(uri) >> requestHeadersSpec
        1 * requestHeadersSpec.retrieve() >> responseSpec
        1 * responseSpec.toEntity(ExchangeRateResponse.class) >> { throw new RuntimeException("An unknown error occured") }
        when:
        exchangeRateRestClient.fetchExchangeRates(baseCurrency)

        then:
        def e = thrown(WebClientException)
        e.message == WEB_CLIENT_GENERAL_EXCEPTION_MESSAGE
    }
}
