package pl.futurecollars.invoicing.memory

import pl.futurecollars.invoicing.db.DataBase
import pl.futurecollars.invoicing.db.FileBasedDatabase
import pl.futurecollars.invoicing.db.IdService
import pl.futurecollars.invoicing.utils.FileService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

class IdServiceTest extends Specification {

    Path nextIdDbPath = Path.of("C:\\Users\\macie\\Documents\\Projects\\invoicing-system-maciek-bella\\app\\src\\test\\resources\\idTest.txt")
    Path wrongPath = Path.of("C:\\Users\\macie\\Documents\\Projects\\invoicing-syste\\app\\src\\test\\resources\\idTest.txt")
//    Path wrong = Path.of("C:\\Users\\macie\\Documents\\Projects\\invoicing-system-maciek-bella\\app\\src\\test\\resources\\idTest.json")
    FileService fileService = new FileService()

    def cleanup() {
        Files.write(nextIdDbPath, [])

    }

    def "next id starts from 1 if file was empty"() {
        given:
        IdService idService = new IdService(nextIdDbPath, fileService)

        when:
        ["1"] == Files.readAllLines(nextIdDbPath)
        def result = idService.getIdAndIncrement()

        then:
        result == 1

    }

    def "next id starts from last number if file was not empty"() {
        given:
        Files.write(nextIdDbPath, ["17"])
        IdService idService = new IdService(nextIdDbPath, fileService)

        when:
        ["17"] == Files.readAllLines(nextIdDbPath)
        def result = idService.getIdAndIncrement()

        then:
        result == 17

    }

    def "next id starts from last numwber if file was not empty"() {
        when:
        IdService idService = new IdService(wrongPath, fileService)
        def result = idService.idAndIncrement

        then:
        def ex = thrown(RuntimeException)
        ex.message == "Failed"
        ex.cause.class == NoSuchFileException
    }
}