package pl.futurecollars.invoicing

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class InvoiceApplicationTest extends Specification {

    def 'should start application context'() {
        setup:
        def app = new InvoiceApplication()

        and:
        app.main()

    }
}
