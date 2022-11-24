package pl.futurecollars.invoicing.utils

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class FileServiceTest extends Specification {

    private FileService fileService = new FileService();
    private Path path = Path.of("app/src/test/resources/lineTest.txt")

    def cleanup() {
        Files.write(path, [])
    }

    def "should line is correctly appended to file"() {
        given:
        def testLine = "Test line to write"
        fileService.appendLineToFile(path, testLine)

        when:
        def result = Files.readAllLines(path)

        then:
        [testLine] == result

    }

    def "should line is correctly written to file"() {

        given:
        fileService.writeToFile(path, "1")
        fileService.writeToFile(path, "2")

        when:
        def result = Files.readAllLines(path)

        then:
        ["1"] == result
        ["2"] == result
    }

    def "should list of lines is correctly written to file"() {
        given:
        def letters = ['a', 'b', 'c']
        fileService.writeLinesToFile(path, letters)

        when:
        def result = Files.readAllLines(path)

        then:
        letters == result

    }

    def "line is correctly read from file"() {
        given:
        def lines = List.of("line 1", "line 2", "line 3")
        Files.write(path, lines)

        when:
        def result = fileService.readAllLines(path)

        then:
        lines == result
    }

}