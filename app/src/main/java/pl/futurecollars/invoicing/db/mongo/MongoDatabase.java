package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.bson.Document;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.Invoice;

@AllArgsConstructor
public class MongoDatabase implements DataBase {

  private final MongoCollection<Invoice> invoices;
  private final MongoIdProvider idProvider;

  @Override
  public long save(Invoice invoice) {
    invoice.setId(idProvider.getNextIdAndIncrement());
    invoices.insertOne(invoice);

    return invoice.getId();
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return Optional.ofNullable(invoices.find(idFilter(id)).first());
  }

  @Override
  public List<Invoice> getAll() {
    return StreamSupport
        .stream(invoices.find().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void update(long id, Invoice invoice) {
    invoice.setId(id);
    invoices.findOneAndReplace(idFilter(id), invoice);
  }

  @Override
  public void delete(long id) {
    invoices.findOneAndDelete(idFilter(id));
  }

  private Document idFilter(long id) {
    return new Document("_id", id);
  }
}
