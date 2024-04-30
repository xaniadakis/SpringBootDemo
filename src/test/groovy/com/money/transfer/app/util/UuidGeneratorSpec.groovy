package com.money.transfer.app.util


import spock.lang.Specification

class UuidGeneratorSpec extends Specification {

    def "handleRuntimeException"() {
        when:
        def uuid = UuidGenerator.generate()

        then:
        uuid.length() == 36 //The typical string representation of a UUID is 36 bytes
    }
}
