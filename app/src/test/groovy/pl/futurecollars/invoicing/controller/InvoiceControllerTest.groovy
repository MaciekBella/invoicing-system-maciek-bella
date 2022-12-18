package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.futurecollars.invoicing.db.InMemoryDataBase
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.TestHelpers.invoice

@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerTest extends Specification{

    @Autowired
    private MockMvc mockMvc
    @Autowired
    private JsonService jsonService
    @Autowired
    private InMemoryDataBase inMemoryDataBase

    private Invoice originalInvoice = invoice(1)

    def 'should return empty list when database is empty'() {
        given:
        def url = "/invoices"
        expect:
        def result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        and:
        result == "[]"
    }

    def 'should not return  list when wrong data is sent'() {
        given:
        def url = "/invoic2es"
        expect:
        def result = mockMvc.perform(get(url))
                .andExpect(status().isNotFound())
    }

    def "should return all invoices"() {
        given:
        def numberOfInvoices = 3
        (1..numberOfInvoices).collect { id ->
            def invoice = invoice(id)
            invoice.id = inMemoryDataBase.save(invoice)
        }
        when:
        def result = mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:

        def resultList = jsonService.toObject(result, Invoice[])
        resultList.size() == numberOfInvoices
    }

    def "should save invoice"() {
        given:
        Invoice invoice = invoice(1)
        def url = "/invoice"

        def invoiceJson = jsonService.toJson(invoice)
        when:
        def result = mockMvc.perform(post(url).content(invoiceJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        result == "4"
        def invoiceOptional = inMemoryDataBase.getById(1)
        invoiceOptional.isPresent()
    }

    def "should not save invoice when wrong data is sent"() {
        given:
        def url = "/invoice"

        expect:
        mockMvc.perform(post(url).content("siema")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }

    def "should update invoice"() {
        given:
        def updateInvoice = originalInvoice
        def url = "/invoice/{}"

        def invoiceAsJson = jsonService.toJson(updateInvoice)
        when:
        def result = mockMvc.perform(put(url ).content(invoiceAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
        then:
        updateInvoice.date == originalInvoice.date
    }

    def "should not update invoice when wrong data is sent"() {
        given:
        Invoice updateInvoice = invoice(1)
        def url = "/inv2ice/{}"
        updateInvoice.id = inMemoryDataBase.save(updateInvoice)
        def invoiceAsJson = jsonService.toJson(updateInvoice)
        expect:
        def result = mockMvc.perform(put(url).content(invoiceAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
    }

    def "invoice can be deleted"() {
        given:
        def url = "/invoice/{}"
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isBadRequest())
    }

    def "invoice can bew deleted when wrong data is sent"() {
        given:
        def url = "/invo2ice/{}"
        expect:
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isNotFound())

    }
}
