package com.money.transfer.app.configuration

import com.money.transfer.app.configuration.AppConfiguration
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

class AppConfigurationSpec extends Specification {

    private AppConfiguration appConfiguration

    def setup(){
        appConfiguration = new AppConfiguration()
    }

    def "test exchangeRateWebClient"(){
        when:
        def client = appConfiguration.exchangeRateWebClient()

        then:
        noExceptionThrown()
        client instanceof WebClient
    }
}
