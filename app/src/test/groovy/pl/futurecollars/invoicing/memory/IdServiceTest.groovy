package pl.futurecollars.invoicing.memory

import pl.futurecollars.invoicing.db.IdService
import pl.futurecollars.invoicing.utils.FileService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class IdServiceTest extends Specification {

    Path nextIdDbPath = Path.of("app/src/test/resources/idTest.txt")
    FileService fileService = new FileService()

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
}
