package pl.futurecollars.invoicing.db

import org.mockito.Mockito
import pl.futurecollars.invoicing.TestHelpers
import spock.lang.Specification

import java.time.LocalDate


class JpaDatabaseTest extends Specification {

    private JpaDatabase jpaDatabase
    private InvoiceRepository invoiceRepository

    def setup() {
        invoiceRepository = Mockito.mock(InvoiceRepository.class)
        jpaDatabase = new JpaDatabase(invoiceRepository)
    }

    def "should save invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def saveInvoice = TestHelpers.invoice(1)
        saveInvoice.id = 1
        Mockito.when(invoiceRepository.save(invoice)).thenReturn(saveInvoice)
        when:
        def result = jpaDatabase.save(invoice)
        then:
        result == 1
    }

    def "should get by id invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)

        Mockito.when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice))
        when:
        def result = jpaDatabase.getById(1)
        then:
        result.isPresent()
        result.get() == invoice
    }

    def "should update invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def updateInvoice = TestHelpers.invoice(2)
        updateInvoice.id = 1
        Mockito.when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice))
        expect:
        jpaDatabase.update(1, updateInvoice)
    }

    def "should throw exception when not found"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def updateInvoice = TestHelpers.invoice(2)
        updateInvoice.id = 1
        Mockito.when(invoiceRepository.findById(1)).thenReturn(Optional.empty())
        when:
        jpaDatabase.update(1, updateInvoice)
        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message == "Invoice with id: 1 does not exist in database"
    }

    def "should get all invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        Mockito.when(invoiceRepository.findAll()).thenReturn(List.of(invoice))
        when:
        def result = jpaDatabase.getAll()
        then:
        result.buyer.name == ["RW INVEST Sp. z o.o"]
        result.date == [LocalDate.now()]
    }

    def "should delete invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        Mockito.when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice))
        expect:
        jpaDatabase.delete(1)
    }
}
