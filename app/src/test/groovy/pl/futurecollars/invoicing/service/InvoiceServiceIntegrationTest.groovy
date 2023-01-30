package pl.futurecollars.invoicing.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.TestHelpers
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

@SpringBootTest
class InvoiceServiceIntegrationTest extends Specification {

    @Autowired
    private InvoiceService service

    def cleanup() {
        service.getAll().forEach(invoice -> service.delete(invoice.getId()))
    }

    def "should save invoices returning sequential id"() {
        given:
        def invoices = [invoice(1), invoice(2)]

        when:
        def ids = invoices.collect({ service.save(it) })

        then:
        ids == service.getAll().collect({ it.id })
        ids.forEach({ assert service.getById(it).isPresent() })
        ids.forEach({ assert service.getById(it).get().getId() == it })
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
        def invoices = [invoice(1), invoice(2)]
        def ids = invoices.collect({ service.save(it) })

        expect:
        service.getAll().size() == invoices.size()
        ids.forEach({ assert service.getById(it).isPresent() })

        when:
        service.delete(ids.get(0))

        then:
        service.getAll().size() == invoices.size() - 1
        service.getAll().forEach({ assert ids.contains(it.id) })
    }

    def "should delete all invoices"() {
        given:
        def invoices = [invoice(1), invoice(2)]
        def ids = invoices.collect({ service.save(it) })

        when:
        ids.forEach({ service.delete(it) })

        then:
        service.getAll().isEmpty()
    }

    def "should the deletion of a non-existent invoice cause any error?"() {
        expect:
        service.delete(123)
    }

    def "should  possible to update the invoice"() {
        given:
        def invoice = invoice(1)
        long id = service.save(invoice)
        def updatedInvoice = TestHelpers.invoice(2)

        when:
        service.update(id, updatedInvoice)

        then:
        def actualInvoice = service.getById(id).get()
        actualInvoice.buyer.taxIdentificationNumber == '2'
        actualInvoice.seller.taxIdentificationNumber == '2'
    }

    def "should updating not existing invoice throws exception"() {
        given:
        def updatedInvoice = invoice(1)

        when:
        service.update(213, updatedInvoice)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Invoice with id: 213 does not exist in database"
    }
}
