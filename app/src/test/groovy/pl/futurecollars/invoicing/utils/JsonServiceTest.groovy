package pl.futurecollars.invoicing.utils


import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification


class JsonServiceTest extends Specification {

    JsonService jsonService = new JsonService()

    def "convert object to json "() {
        given:
        def invoiceInJson = "{\"id\":0,\"date\":[2022,11,22],\"buyer\":{\"taxIdentificationNumber\":\"5296472303\",\"address\":\"ul. LÄ…czna 43 03-156 Lipinki, Polska\",\"name\":\"RW INVEST Sp. z o.o\"},\"seller\":{\"taxIdentificationNumber\":\"2521342600\",\"address\":\"32-005 Zielona GĂłra, Kaszkietowa 19\",\"name\":\"CD-Project\"},\"entries\":[{\"description\":\"Programming course\",\"price\":10000,\"vatValue\":2300,\"vatRate\":\"VAT_23\"}]}"
        when:
        def result = jsonService.toObject(invoiceInJson, Invoice.class)
        then:
        result.id == 0
        result.buyer.name == "RW INVEST Sp. z o.o"
    }

    def "should throw exception whe json is invalid"() {
        given:
        def invoiceInJson = ""
        when:
        jsonService.toObject(invoiceInJson, Invoice.class)
        then:
        def exception = thrown(RuntimeException)
        exception.message == "Cannot parse json"
    }

    def "Should map json to object"() {
        given:
        def invoiceInJson = TestHelpers.company(0)
        when:
        def result1 = jsonService.toJson(invoiceInJson)
        then:
        result1 == "{\"taxIdentificationNumber\":\"0\",\"address\":\"ul. Lączna 43 03-156 Lipinki, Polska\",\"name\":\"RW INVEST Sp. z o.o\"}"

    }
}
