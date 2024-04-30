package com.money.transfer.app.controller.v1

import com.money.transfer.app.dto.TransferRequestBody
import com.money.transfer.app.dto.TransferResponseBody
import com.money.transfer.app.service.MoneyTransferServiceImpl
import org.springframework.http.HttpStatusCode
import spock.lang.Specification

class MoneyTransferControllerImplSpec extends Specification {

    private MoneyTransferServiceImpl moneyTransferService

    private MoneyTransferControllerImpl moneyTransferControllerImpl

    def setup() {
        moneyTransferService = Mock(MoneyTransferServiceImpl)
        moneyTransferControllerImpl = new MoneyTransferControllerImpl(moneyTransferService)
    }

    def "test transfer success"() {
        given:
        def transferResponseBody = TransferResponseBody.builder()
                .response("Transfer Successful")
                .build()
        def transferRequestBody = TransferRequestBody.builder()
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .amount(128.5)
                .currency("EUR")
                .build()
        moneyTransferService.transfer(transferRequestBody) >> transferResponseBody
        when:
        def response = moneyTransferControllerImpl.transfer(transferRequestBody)

        then:
        response.getStatusCode() == HttpStatusCode.valueOf(200)
        response.getBody() instanceof TransferResponseBody
        response.getBody().getResponse() == transferResponseBody.getResponse()
    }

    def "test transfer fail"() {
        given:
        def transferRequestBody = TransferRequestBody.builder()
                .sourceAccountId("sourceAccountId")
                .targetAccountId("targetAccountId")
                .amount(128.5)
                .currency("EUR")
                .build()
        moneyTransferService.transfer(transferRequestBody) >> new RuntimeException()

        when:
        def response = moneyTransferControllerImpl.transfer(transferRequestBody)

        then:
        thrown(RuntimeException)
        response == null
    }


}
