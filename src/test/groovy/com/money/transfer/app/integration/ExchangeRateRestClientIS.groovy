package com.money.transfer.app.integration


import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

/**
 * Integration test for the ExchangeRateRestClient that verifies the functionality of communicating
 * with the external exchange rate API and successfully coverting a currency with the info fetched.
 */
class ExchangeRateRestClientIS extends Specification {


    private ExchangeRateRestClient exchangeRateRestClient

    private WebClient exchangeRateWebClient


    def setup() {
        exchangeRateWebClient = WebClient.builder()
                .baseUrl('https://v6.exchangerate-api.com/v6/5eba9759ea7dbf15cc278c52/latest/')
                .build()
        exchangeRateRestClient = new ExchangeRateRestClient(exchangeRateWebClient)
    }

    def "test fetch exchange rates"() {
        given:
        def oldAmount = 25
        def oldCurrency = "EUR"
        def newCurrency = "USD"

        when:
        def response = exchangeRateRestClient.fetchExchangeRates(oldCurrency)
        def newAmount = oldAmount * response.conversion_rates.get(newCurrency)

        then:
        noExceptionThrown()
        println(response.result)
        println response.conversion_rates
        println oldAmount + oldCurrency + " == " + newAmount + newCurrency
    }
}
