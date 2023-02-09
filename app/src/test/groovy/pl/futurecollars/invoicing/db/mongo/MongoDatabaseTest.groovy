package pl.futurecollars.invoicing.db.mongo

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import org.bson.Document
import org.mockito.Mockito
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class MongoDatabaseTest extends Specification {

    private MongoDatabase mongoDatabase;
    private MongoIdProvider mongoIdProvider;
    private MongoCollection<Invoice> invoiceMongoCollection;


    def setup() {
        mongoIdProvider = Mockito.mock(MongoIdProvider.class)
        invoiceMongoCollection = Mockito.mock(MongoCollection.class)
        mongoDatabase = new MongoDatabase(invoiceMongoCollection, mongoIdProvider)
    }

    def "should save invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        Mockito.when(mongoIdProvider.getNextIdAndIncrement()).thenReturn(2L)
        when:
        def result = mongoDatabase.save(invoice)
        then:
        result == 2
    }

    def "should get by id invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        FindIterable<Invoice> invoices = Mockito.mock(FindIterable.class)
        Mockito.when(invoiceMongoCollection.find(new Document("_id",1))).thenReturn(invoices)
        Mockito.when(invoices.first()).thenReturn(invoice)
        when:
        def result = mongoDatabase.getById(1)
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
}