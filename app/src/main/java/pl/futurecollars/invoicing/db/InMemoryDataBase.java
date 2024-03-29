package pl.futurecollars.invoicing.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoicing.model.Invoice;

public class InMemoryDataBase implements DataBase {

  private Map<Long, Invoice> invoices = new HashMap<>();
  private long nextId = 1;

  @Override
  public long save(Invoice invoice) {
    invoice.setId(nextId);
    invoices.put(nextId, invoice);
    return nextId++;
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public List<Invoice> getAll() {
    return new ArrayList<>(invoices.values());
  }

  @Override
  public void update(long id, Invoice invoice) {
    if (!invoices.containsKey(id)) {
      throw new IllegalArgumentException("Invoice with id: " + id + " does not exist in database");
    }
    invoice.setId(id);
    invoices.put(id, invoice);
  }

  @Override
  public void delete(long id) {
    invoices.remove(id);
  }

}
