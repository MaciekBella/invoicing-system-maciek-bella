package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class JpaDatabase implements DataBase {

  private final InvoiceRepository invoiceRepository;

  @Override
  public long save(Invoice invoice) {
    return invoiceRepository.save(invoice).getId();
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return invoiceRepository.findById(id);
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoiceRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(long id, Invoice invoice) {
    Optional<Invoice> invoiceOptional = invoiceRepository.findById(id);

    if (invoiceOptional.isPresent()) {
      Invoice odlInvoice = invoiceOptional.get();

      invoice.setId(id);
      invoice.getBuyer().setId(odlInvoice.getBuyer().getId());
      invoice.getSeller().setId(odlInvoice.getSeller().getId());

      invoiceRepository.save(invoice);
    } else {
      throw new IllegalArgumentException("Invoice with id: " + id + " does not exist in database");
    }
  }

  @Override
  public void delete(long id) {
    Optional<Invoice> invoice = invoiceRepository.findById(id);

    invoice.ifPresent(invoiceRepository::delete);
  }
}
