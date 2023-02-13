package pl.futurecollars.invoicing.db.mongo

import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import org.bson.conversions.Bson
import org.mockito.Mockito
import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

class MongoDatabaseTest extends Specification {

    private MongoDatabase mongoDatabase
    private MongoIdProvider mongoIdProvider
    private MongoCollection<Invoice> invoiceMongoCollection

    def setup() {
        mongoIdProvider = Mockito.mock(MongoIdProvider.class)
        invoiceMongoCollection = Stub()
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
        invoiceMongoCollection.find(_) >> Stub(FindIterable.class) { it.first() >> invoice }
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
        invoiceMongoCollection.findOneAndReplace(_, invoice) >> Stub(FindIterable.class)
        expect:
        mongoDatabase.update(1, updateInvoice)
    }

    def "should throw exception when not found"() {
        given:
        def invoice = TestHelpers.invoice(1)
        def updateInvoice = TestHelpers.invoice(2)
        updateInvoice.id = 1
        invoiceMongoCollection.findOneAndReplace(_, _) >> null
        when:
        mongoDatabase.update(1, updateInvoice)
        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message == "Invoice with id: 1 does not exist in database"
    }


    def "should get all invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        invoiceMongoCollection.find() >> Stub(FindIterable.class) {
            it.spliterator() >> [invoice].spliterator()
        }
        when:
        def result = mongoDatabase.getAll()
        then:
        result.buyer.name == ["RW INVEST Sp. z o.o"]
        result.seller.address == ["ul. Lączna 43 03-156 Lipinki, Polska"]
    }

    def "should delete invoice"() {
        given:
        def invoice = TestHelpers.invoice(1)
        invoiceMongoCollection.findOneAndDelete(1L as Bson)
        expect:
        mongoDatabase.delete(1)
    }
}