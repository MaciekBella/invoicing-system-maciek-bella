package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.WithId;

public class InMemoryDatabase<T extends WithId> implements DataBase<T> {

  private Map<Long, T> invoices = new HashMap<>();
  private long nextId = 1;

  @Override
  public long save(T item) {
    item.setId(nextId);
    invoices.put(nextId, item);
    return nextId++;
  }

  @Override
  public Optional<T> getById(long id) {
    return Optional.ofNullable(invoices.get(id));
  }

  @Override
  public List<T> getAll() {
    return new ArrayList<>(invoices.values());
  }

  @Override
  public void update(long id, T invoice) {
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
