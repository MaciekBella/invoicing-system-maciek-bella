package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.futurecollars.invoicing.model.Invoice;

@Api(tags = {"invoice-controller"})
public interface InvoiceApi {

  @PostMapping
  @ApiOperation(value = "Add invoice")
  long add(@RequestBody Invoice invoice);

  @GetMapping
  @ApiOperation(value = "Get All Invoices")
  List<Invoice> getAllInvoices();

  @GetMapping("/{id}")
  @ApiOperation(value = "Get invoice by Id")
  Optional<Invoice> getById(@PathVariable long id);

  @PutMapping("/{id}")
  @ApiOperation(value = "Update invoice by Id")
  void update(@PathVariable long id, @RequestBody Invoice invoice);

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete invoice by Id")
  void delete(@PathVariable long id);
}
