package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.model.Invoice;

public interface DataBase {

  long save(Invoice invoice);

  Optional<Invoice> getById(long id);

  List<Invoice> getAll();

  void update(long id, Invoice invoice);

  void delete(long id);

}
