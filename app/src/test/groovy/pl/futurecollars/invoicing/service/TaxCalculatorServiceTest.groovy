package pl.futurecollars.invoicing.service

import org.mockito.Mockito
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.db.DataBase
import spock.lang.Specification

class TaxCalculatorServiceTest extends Specification {

    private TaxCalculatorService taxCalculatorService
    private DataBase dataBase

    def setup() {
        dataBase = Mockito.mock(DataBase.class)
        taxCalculatorService = new TaxCalculatorService(dataBase)
    }

    def "should return sum of products if NIP matches"() {
        given:
        def invoice = TestHelpers.invoiceWithNIP("5")
        Mockito.when(dataBase.getAll()).thenReturn([invoice])
        when:
        def taxCalculatorResponse = taxCalculatorService.calculateTaxes("5")
        then:
        taxCalculatorResponse.income == 5000
        taxCalculatorResponse.costs == 5000
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 400
        taxCalculatorResponse.outgoingVat == 400
        taxCalculatorResponse.vatToReturn == 0
    }

    def "should return zero if there is no invoice"() {
        given:
        Mockito.when(dataBase.getAll()).thenReturn([])
        when:
        def taxCalculatorResponse = taxCalculatorService.calculateTaxes("0")

        then:
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }
}