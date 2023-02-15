package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.bson.Document;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.WithId;

@AllArgsConstructor
public class MongoDatabase<T extends WithId> implements DataBase<T> {

  private final MongoCollection<T> invoices;
  private final MongoIdProvider idProvider;

  @Override
  public long save(T item) {
    item.setId(idProvider.getNextIdAndIncrement());
    invoices.insertOne(item);

    return item.getId();
  }

  @Override
  public Optional<T> getById(long id) {
    return Optional.ofNullable(invoices.find(idFilter(id)).first());
  }

  @Override
  public List<T> getAll() {
    return StreamSupport
        .stream(invoices.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(long id, T invoice) {
    invoice.setId(id);

    if (invoices.findOneAndReplace(idFilter(id), invoice) == null) {
      throw new IllegalArgumentException("Invoice with id: " + id + " does not exist in database");
    }
  }

  @Override
  public void delete(long id) {
    invoices.findOneAndDelete(idFilter(id));
  }

  private Document idFilter(long id) {
    return new Document("_id", id);
  }
}
