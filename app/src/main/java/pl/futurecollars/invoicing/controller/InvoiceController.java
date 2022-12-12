package pl.futurecollars.invoicing.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
public class InvoiceController {

  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @GetMapping("/hello")
  public String helloWorld() {
    return "Hello Man";
  }

  @PostMapping("/invoice")
  public long add(@RequestBody Invoice invoice) {
    return invoiceService.save(invoice);
  }

  @GetMapping("/invoices")
  public List<Invoice> getAllInvoices() {
    return invoiceService.getAll();
  }

  @GetMapping("/invoice/{id}")
  public Optional<Invoice> getById(@PathVariable long id) {
    return invoiceService.getById(id);
  }

  @PutMapping("/invoice/{id}")
  public void update(@PathVariable long id, @RequestBody Invoice invoice) {
    invoiceService.update(id, invoice);
  }

  @DeleteMapping("/invoice/{id}")
  public void delete(@PathVariable long id) {
    invoiceService.delete(id);
  }
}
