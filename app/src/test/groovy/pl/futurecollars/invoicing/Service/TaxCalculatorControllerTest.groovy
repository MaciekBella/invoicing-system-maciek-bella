package pl.futurecollars.invoicing.Service

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
class TaxCalculatorControllerTest extends Specification {

    @Autowired
    TaxCalculatorService taxCalculatorService;
    @Autowired
    JsonService jsonService
    @Autowired
    MockMvc mockMvc


    long addInvoiceAndReturnId(String invoiceAsJson) {
        Integer.valueOf(
                mockMvc.perform(
                        post("/invoices")
                                .content(invoiceAsJson)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andReturn()
                        .response
                        .contentAsString
        )
    }

    List<Invoice> addInvoices(long count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(jsonService.toJson(invoice))
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
        addInvoices(10)

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
}