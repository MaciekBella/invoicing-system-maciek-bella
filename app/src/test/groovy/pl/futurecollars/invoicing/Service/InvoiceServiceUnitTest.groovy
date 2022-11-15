package pl.futurecollars.invoicing.Service

import pl.futurecollars.invoicing.db.DataBase
import pl.futurecollars.invoicing.service.InvoiceService
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

class InvoiceServiceUnitTest extends Specification {

    private InvoiceService service
    private DataBase dataBase

    def setup() {
        dataBase = Mock()
        service = new InvoiceService(dataBase)
    }

    def "calling save() should delegate to database save() method"() {
        given:
        def invoice = invoice(1)
        when:
        service.save(invoice)
        then:
        1 * dataBase.save(invoice)
    }

    def "calling delete() should delegate to database delete() method"() {
        given:
        def invoiceId = 123
        when:
        service.delete(invoiceId)
        then:
        1 * dataBase.delete(invoiceId)
    }

    def "calling getById() should delegate to database getById() method"() {
        given:
        def invoiceId = 321
        when:
        service.getById(invoiceId)
        then:
        1 * dataBase.getById(invoiceId)
    }

    def "calling getAll() should delegate to database getAll() method"() {
        when:
        service.getAll()
        then:
        1 * dataBase.getAll()
    }

    def "calling update() should delegate to database update() method"() {
        given:
        def invoice = invoice(1)
        when:
        service.update(invoice.getId(), invoice)
        then:
        1 * dataBase.update(invoice.getId(), invoice)
    }
}