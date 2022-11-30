package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path


class FileBasedDatabaseTest extends Specification {

//    Path idPath = Path.of("C:\\Users\\macie\\Documents\\Projects\\invoicing-system-maciek-bella\\app\\src\\test\\resources\\idTestFile.txt")
//    Path dbPath = Path.of("C:\\Users\\macie\\Documents\\Projects\\invoicing-system-maciek-bella\\app\\src\\test\\resources\\dbTestFile.txt")
//    Path wrong = Path.of("C:\\Users\\macie\\Documents\\Projectssystem-maciek-bella\\app\\src\\test\\resources\\dbTestFile.txt")
//    FileService fileService = new FileService()
//    JsonService jsonService = new JsonService()
//    IdService idService = new IdService(idPath, fileService)
//    DataBase fileBased = new FileBasedDatabase(dbPath, idService, fileService, jsonService)
//    DataBase wrongFileDataBase = new FileBasedDatabase(wrong, idService, fileService, jsonService)
//    Invoice invoice = TestHelpers.invoice(0)
//    Invoice updateInvoice = TestHelpers.updateInvoice(1)
//
//    def cleanup() {
//        Files.write(idPath, [])
//        Files.write(dbPath, [])
//    }
//
//    def "Should save invoice"() {
//        when:
//        def result = fileBased.save(invoice)
//        then:
//        result == 1
//    }
//
//    def "should throw exception with invoice saving error"() {
//        when:
//        def result = wrongFileDataBase.save(invoice)
//        then:
//        def ex = thrown(RuntimeException)
//        ex.message == "Database failed to save invoice"
//    }
//
//    def "should throw exception with id error"() {
//        when:
//        wrongFileDataBase.getById(1)
//
//        then:
//        def exception = thrown(RuntimeException)
//        exception.message == "Database failed to get invoice with id: 1"
//        exception.cause.class == NoSuchFileException
//    }
//
//
//    def "should throw an exception with no existing invoice ID"() {
//        when:
//        fileBased.update(2, invoice)
//        def result = fileBased.getById(2)
//
//        then:
//        def ex = thrown(RuntimeException)
//        ex.message == "Invoice with id: 2 does not exist in database"
//    }
//
//    def "should throw an exception with an update invoice error"() {
//        when:
//        wrongFileDataBase.update(1, invoice)
//        def result = wrongFileDataBase.getById(1)
//
//        then:
//        def ex = thrown(RuntimeException)
//        ex.message == "Failed to update 1"
//        ex.cause.class == NoSuchFileException
//    }
}
