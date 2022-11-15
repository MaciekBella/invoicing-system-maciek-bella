package pl.futurecollars.invoicing.Service

import pl.futurecollars.invoicing.db.DataBase
import pl.futurecollars.invoicing.db.InMemoryDataBase
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.InvoiceService
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

class InvoiceServiceIntegrationTest extends Specification {

    private InvoiceService service
    private List<Invoice> invoices

    def setup() {
        DataBase db = new InMemoryDataBase()
        service = new InvoiceService(db)

        invoices = (1..12).collect { invoice(it) }
    }

    def "should save invoices returning sequential id"() {
        when:
        def ids = invoices.collect({ service.save(it) })

        then:
        ids == (1..invoices.size()).collect()
        ids.forEach({ assert service.getById(it).isPresent() })
        ids.forEach({ assert service.getById(it).get().getId() == it })
        ids.forEach({ assert service.getById(it).get() == invoices.get(it - 1 as int) })
    }

    def "should get by id returns empty optional when there is no invoice with given id"() {
        expect:
        !service.getById(1).isPresent()
    }

    def "get all returns empty collection if there were no invoices"() {
        expect:
        service.getAll().isEmpty()
    }

    def "should receive all returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({ service.save(it) })

        expect:
        service.getAll().size() == invoices.size()
        service.getAll().forEach({ assert it == invoices.get(it.getId() - 1 as int) })

        when:
        service.delete(1)

        then:
        service.getAll().size() == invoices.size() - 1
        service.getAll().forEach({ assert it == invoices.get(it.getId() - 1 as int) })
        service.getAll().forEach({ assert it.getId() != 1 })
    }

    def "should delete all invoices"() {
        given:
        invoices.forEach({ service.save(it) })

        when:
        invoices.forEach({ service.delete(it.getId()) })

        then:
        service.getAll().isEmpty()
    }

    def "should the deletion of a non-existent invoice cause any error?"() {
        expect:
        service.delete(123)
    }

    def "should  possible to update the invoice"() {
        given:
        long id = service.save(invoices.get(0))

        when:
        service.update(id, invoices.get(1))

        then:
        service.getById(id).get() == invoices.get(1)
    }

    def "should updating not existing invoice throws exception"() {
        when:
        service.update(213, invoices.get(1))

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Invoice with id: 213 does not exist in database"
    }
}
