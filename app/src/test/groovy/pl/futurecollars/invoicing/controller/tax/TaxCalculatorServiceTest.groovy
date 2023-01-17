package pl.futurecollars.invoicing.controller.tax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorResult
import pl.futurecollars.invoicing.service.TaxCalculatorService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.TestHelpers.invoice

@AutoConfigureMockMvc
@SpringBootTest
class TaxCalculatorServiceTest extends Specification {

    @Autowired
    private JsonService jsonService
    @Autowired
    private MockMvc mockMvc

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

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

    List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, Invoice[])
    }

    def deleteInvoice(long id) {
        mockMvc.perform(delete("/invoices/$id"))
                .andExpect(status().isNoContent())
    }

    TaxCalculatorResult calculateTax(String taxIdentificationNumber) {
        def response = mockMvc.perform(get("/tax/$taxIdentificationNumber"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.toObject(response, TaxCalculatorResult)
    }

    def "should return zero if there is no invoice"() {
        when:
        def taxCalculatorResponse = calculateTax("0")

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
        def taxCalculatorResponse = calculateTax("1")

        then:
        taxCalculatorResponse.income == 1000
        taxCalculatorResponse.costs == 1000
        taxCalculatorResponse.earnings == 0
        taxCalculatorResponse.incomingVat == 80
        taxCalculatorResponse.outgoingVat == 80
        taxCalculatorResponse.vatToReturn == 0
    }
}