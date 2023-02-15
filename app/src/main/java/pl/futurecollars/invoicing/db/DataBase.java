package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.Optional;
import pl.futurecollars.invoicing.model.WithId;

public interface DataBase<T extends WithId> {

  long save(T item);

  Optional<T> getById(long id);

  List<T> getAll();

  void update(long id, T updateItem);

  void delete(long id);

}
