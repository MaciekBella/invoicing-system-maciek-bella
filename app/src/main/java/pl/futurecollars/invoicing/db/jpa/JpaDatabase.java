package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.WithId;

@AllArgsConstructor
public class JpaDatabase<T extends WithId> implements DataBase<T> {

  private final CrudRepository<T, Long> repository;

  @Override
  public long save(T item) {
    return repository.save(item).getId();
  }

  @Override
  public Optional<T> getById(long id) {
    return repository.findById(id);
  }

  @Override
  public List<T> getAll() {
    return StreamSupport
        .stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(long id, T invoice) {
    Optional<T> invoiceOptional = repository.findById(id);

    if (invoiceOptional.isPresent()) {
      repository.save(invoice);
    } else {
      throw new IllegalArgumentException("Invoice with id: " + id + " does not exist in database");
    }
  }

  @Override
  public void delete(long id) {
    Optional<T> invoice = repository.findById(id);

    invoice.ifPresent(repository::delete);
  }
}
