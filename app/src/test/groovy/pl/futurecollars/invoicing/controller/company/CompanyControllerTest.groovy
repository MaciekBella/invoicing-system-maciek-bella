package pl.futurecollars.invoicing.controller.company

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.futurecollars.invoicing.db.DataBase
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.TestHelpers.company

@AutoConfigureMockMvc
@SpringBootTest
class CompanyControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc
    @Autowired
    private JsonService jsonService
    @Autowired
    private DataBase<Company> database

    def cleanup() {
        database.getAll().forEach(company -> database.delete(company.getId()))
    }

    def 'should return empty list when database is empty'() {
        given:
        def url = "/companies"
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
        def company = company(1)
        def id = database.save(company)
        def url = "/companies/$id"
        when:
        def result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        def actualInvoice = jsonService.toObject(result, Company.class)
        actualInvoice.id == id
    }


    def "should return all invoices"() {
        given:
        def numberOfCompanies = 4
        (1..numberOfCompanies).collect { id ->
            def company = company(id)
            company.id = database.save(company)
        }

        when:
        def result = mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        def resultList = jsonService.toObject(result, Company[])
        resultList.size() == numberOfCompanies

    }

    def "should save invoice"() {
        given:
        def company = company(1)
        def url = "/companies"

        def companyJson = jsonService.toJson(company)
        when:
        def result = mockMvc.perform(post(url).content(companyJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        def savedInvoice = database.getById(result as Long)
        savedInvoice.isPresent()
        savedInvoice.get().id == result as Long
        savedInvoice.get().taxIdentificationNumber == '1'
    }

    def "should not save invoice when wrong data is sent"() {
        given:
        def url = "/companies"

        expect:
        mockMvc.perform(post(url).content("siema")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }

    def "should update invoice"() {
        given:
        def company = company(1)
        company.id = database.save(company)
        def updateCompany = company
        updateCompany.taxIdentificationNumber = 1
        def url = "/companies/$company.id"

        def companyAsJson = jsonService.toJson(updateCompany)
        when:
        mockMvc.perform(put(url).content(companyAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        def invoiceOptional = database.getById(company.id)
        then:
        invoiceOptional.isPresent()
        invoiceOptional.get().taxIdentificationNumber == updateCompany.taxIdentificationNumber

    }

    def "should not update invoice when wrong data is sent"() {
        given:
        Company company = company(1)
        database.save(company)
        def url = "/invoices/$company.id"
        expect:
        mockMvc.perform(put(url).content("elo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }

    def "should delete the invoice"() {
        given:
        def company = company(1)
        database.save(company)
        def url = "/companies/$company.id"
        when:
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        then:
        def invoiceResult = database.getById(company.id)
        invoiceResult.isEmpty()
    }
}
