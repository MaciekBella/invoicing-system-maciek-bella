package pl.futurecollars.invoicing

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelpers {

    static company(long id) {
        Company.builder()
                .taxIdentificationNumber("$id")
                .address("ul. Lączna 43 03-156 Lipinki, Polska")
                .name(("RW INVEST Sp. z o.o"))
                .build()
    }

    static product(long id) {
        InvoiceEntry.builder()
                .description("Programming course $id")
                .price(BigDecimal.valueOf(id * 1000))
                .vatValue(BigDecimal.valueOf(id * 1000 * 0.08))
                .vatRate(Vat.VAT_8)
                .build()
    }

    static invoice(long id) {
        Invoice.builder()
                .date(LocalDate.now())
                .seller(company(id))
                .buyer(company(id))
                .entries(List.of(product(id)))
                .build()
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
