package pl.futurecollars.invoicing

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelpers {

    static company(long id) {
        new Company(("$id").repeat(1),
                "ul. Lączna 43 03-156 Lipinki, Polska",
                "RW INVEST Sp. z o.o")
    }

    static product(long id) {
        new InvoiceEntry(
                "Programming course $id",
                BigDecimal.valueOf(id * 1000),
                BigDecimal.valueOf(id * 1000 * 0.08),
                Vat.VAT_8)
    }

    static invoice(long id) {
        new Invoice(
                LocalDate.now(),
                company(id),
                company(id),
                List.of(product(id)))
    }

    static invoiceWithNIP(String nip) {
        new Invoice(
                LocalDate.now(),
                companyWithNIP(nip),
                companyWithNIP(nip),
                List.of(product(5)))
    }

    static companyWithNIP(String nip) {
        new Company(nip,
                "ul. Lączna 43 03-156 Lipinki, Polska",
                "RW INVEST Sp. z o.o")
    }
}
