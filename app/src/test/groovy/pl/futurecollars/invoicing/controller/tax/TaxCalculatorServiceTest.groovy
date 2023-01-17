package pl.futurecollars.invoicing.controller.tax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.TestHelpers.invoice

@AutoConfigureMockMvc
@SpringBootTest
class TaxCalculatorServiceTest extends Specification {

    @Autowired
    private TaxCalculatorService taxCalculatorService
    @Autowired
    private JsonService jsonService
    @Autowired
    private MockMvc mockMvc

    def addInvoiceAndReturnId(String invoiceAsJson) {
        mockMvc.perform(
                post("/invoices")
                        .content(invoiceAsJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

    }

    private List<Invoice> addInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(jsonService.toJson(invoice)) as long
            return invoice
        }
    }


    def "should return zero if there is no invoice"() {
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

    def "should return sum of products if NIP matches"() {
        given:
        addInvoices(1)
        when:
        def taxCalculatorResponse = taxCalculatorService.calculateTaxes("1")

        then:
        taxCalculatorResponse.income == 1000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 80
        taxCalculatorResponse.outgoingVat == 80
        taxCalculatorResponse.vatToReturn == 0
    }
}