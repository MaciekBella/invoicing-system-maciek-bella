package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.api.InvoiceApi;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@Api(tags = "invoice-controller")
public class InvoiceController implements InvoiceApi {

  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @Override
  public long add(@RequestBody Invoice invoice) {
    return invoiceService.save(invoice);
  }

  @Override
  public List<Invoice> getAllInvoices() {
    return invoiceService.getAll();
  }

 @Override
  public Optional<Invoice> getById(@PathVariable long id) {
    return invoiceService.getById(id);
  }

  @Override
  public void update(@PathVariable long id, @RequestBody Invoice invoice) {
    invoiceService.update(id, invoice);
  }

 @Override
  public void delete(@PathVariable long id) {
    invoiceService.delete(id);
  }
}
