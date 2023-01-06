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

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.TestHelpers.invoice

@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc
    @Autowired
    private JsonService jsonService
    @Autowired
    private InMemoryDataBase inMemoryDataBase

    def cleanup() {
        inMemoryDataBase.deleteAll()
    }

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

    def "should return invoice"() {
        given:
        def invoice = invoice(1)
        inMemoryDataBase.save(invoice)
        def url = "/invoices/1"
        when:
        def result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        def invoices = jsonService.toObject(result, Invoice.class)
        invoices.id == 1
        invoices.date == invoice.date
    }


    def "should return all invoices"() {
        given:
        def numberOfInvoices = 4
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
        def url = "/invoices"

        def invoiceJson = jsonService.toJson(invoice)
        when:
        def result = mockMvc.perform(post(url).content(invoiceJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        result == "6"
    }

    def "should not save invoice when wrong data is sent"() {
        given:
        def url = "/invoices"

        expect:
        mockMvc.perform(post(url).content("siema")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }

    def "should update invoice"() {
        given:
        def invoice = invoice(1)
       invoice.id = inMemoryDataBase.save(invoice)
        def updateInvoice = invoice
        updateInvoice.date = LocalDate.of(2000, 11, 23)
        def url = "/invoices/$invoice.id"

        def invoiceAsJson = jsonService.toJson(updateInvoice)
        when:
        mockMvc.perform(put(url).content(invoiceAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        def invoiceOptional = inMemoryDataBase.getById(invoice.id)
        then:
        invoiceOptional.isPresent()
        invoiceOptional.get().date == updateInvoice.date

    }

    def "should not update invoice when wrong data is sent"() {
        given:
        Invoice invoice = invoice(1)
        inMemoryDataBase.save(invoice)
        def url = "/invoices/$invoice.id"
        expect:
        mockMvc.perform(put(url).content("elo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }

    def "should delete the invoice"() {
        given:
        def invoice = invoice(1)
        inMemoryDataBase.save(invoice)
        def url = "/invoices/$invoice.id"
        when:
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        def invoiceResult = inMemoryDataBase.getById(invoice.id)
        invoiceResult.isEmpty()
    }
}
