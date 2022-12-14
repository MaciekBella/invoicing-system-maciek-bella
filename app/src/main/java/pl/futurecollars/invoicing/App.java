/*
 * This Java source file was generated by the Gradle 'init' task.
 */

package pl.futurecollars.invoicing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.db.InMemoryDataBase;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;
import pl.futurecollars.invoicing.service.InvoiceService;

public class App {

  public String getGreeting() {
    return "Hello World!";
  }

  public static void main(String[] args) {
    System.out.println(new App().getGreeting());

    DataBase db = new InMemoryDataBase();
    InvoiceService service = new InvoiceService(db);

    Company buyer = new Company("5296472303", "ul. Lączna 43 03-156 Lipinki, Polska", "RW INVEST Sp. z o.o");
    Company seller = new Company("2521342600", "32-005 Zielona Góra, Kaszkietowa 19", "CD-Project");

    List<InvoiceEntry> products = List.of(new InvoiceEntry("Programming course", BigDecimal.valueOf(10000), BigDecimal.valueOf(2300), Vat.VAT_23));

    Invoice invoice = new Invoice(LocalDate.now(), buyer, seller, products);

    long id = service.save(invoice);

    service.getById(id).ifPresent(System.out::println);

    System.out.println(service.getAll());

    service.delete(id);

  }
}
