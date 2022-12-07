package pl.futurecollars.invoicing.controller;

import java.util.List;
import java.util.Optional;
import lombok.Generated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.db.InMemoryDataBase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@Generated
@RestController
public class InvoiceController {

  private final InvoiceService invoiceService = new InvoiceService(new InMemoryDataBase());

  @GetMapping("/hello")
  public String helloWorld() {
    return "Hello Man";
  }

  @PostMapping("/add-invoice")
  public long add(@RequestParam Invoice invoice) {
    return invoiceService.save(invoice);
  }

  @GetMapping("/invoices")
  public List<Invoice> getAllInvoices() {
    return invoiceService.getAll();
  }

  @GetMapping("/invoice")
  public Optional<Invoice> getById(@RequestParam long id) {
    return invoiceService.getById(id);
  }

  @PutMapping("/update-Invoice")
  public void update(@RequestParam long id, @RequestParam Invoice invoice) {
    invoiceService.update(id, invoice);
  }

  @DeleteMapping("/Delete-Invoice")
  public void delete(@RequestParam long id) {
    invoiceService.delete(id);
  }
}
