package pl.futurecollars.invoicing

import spock.lang.Specification

class InvoiceApplicationTest extends Specification {

    def 'should start application context'() {
        setup:
        def app = new InvoiceApplication()

        and:
        app.main()

    }
}
