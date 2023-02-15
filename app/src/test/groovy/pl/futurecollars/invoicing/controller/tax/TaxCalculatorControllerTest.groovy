package pl.futurecollars.invoicing.controller.tax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.db.DataBase
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorResult
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
class TaxCalculatorControllerTest extends Specification {

    @Autowired
    private JsonService jsonService
    @Autowired
    private DataBase<Invoice> dataBase
    @Autowired
    private MockMvc mockMvc

    def cleanup() {
        dataBase.getAll().forEach(invoice -> dataBase.delete(invoice.getId()))
    }

    def "should return sum of products if NIP matches"() {
        given:
        def invoice = TestHelpers.invoiceWithNIP("5")
        dataBase.save(invoice)
        def url = "/tax/5"
        when:
        def result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        def taxCalculatorResponse = jsonService.toObject(result, TaxCalculatorResult.class)
        taxCalculatorResponse.income == 5000
        taxCalculatorResponse.costs == 5000
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 400
        taxCalculatorResponse.outgoingVat == 400
        taxCalculatorResponse.vatToReturn == 0
    }

    def "should return zero if there is no invoice"() {
        given:
        def url = "/tax/0"
        when:
        def result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        def taxCalculatorResponse = jsonService.toObject(result, TaxCalculatorResult.class)
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 0
        taxCalculatorResponse.outgoingVat == 0
        taxCalculatorResponse.vatToReturn == 0
    }
}
