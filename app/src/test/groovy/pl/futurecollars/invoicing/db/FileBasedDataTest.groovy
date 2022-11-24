package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileBasedDataTest extends Specification {

    Path idPath = Path.of("app/src/test/resources/idTestFile.txt")
    Path dbPath = Path.of("app/src/test/resources/dbTestFile.txt")
    FileService fileService = new FileService()
    JsonService jsonService = new JsonService()
    IdService idService = new IdService(idPath, fileService)
    DataBase fileBased = new FileBasedDatabase(dbPath, idService, fileService, jsonService)


    def cleanup() {
        Files.write(idPath, [])
        Files.write(dbPath, [])
    }

    def "Should"() {
        given:
        def invoice = TestHelpers.invoice(0)
        when:
        def result = fileBased.save(invoice)
        then:
        result == 1
    }

    def "should"() {
        given:
        def invoice = TestHelpers.invoice(0)
        fileBased.save(invoice)
        when:
        def result = fileBased.getById(0)
        then:
        result.isPresent()
        def invoiceResult = result.get()
        invoiceResult.id == 0
        invoiceResult.buyer.name == invoice.buyer.name

    }

    def "should update invoice"() {
        given:
        def invoice = TestHelpers.invoice(0)
        fileBased.save(invoice)

        when:
        fileBased.update(1, invoice)
        def result = fileBased.getById(1)

        then:
        result.isPresent()
        result.toString().contains("id=1")
    }
}