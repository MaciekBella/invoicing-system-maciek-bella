package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.WithId;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements DataBase<T> {

  private final Path path;
  private final IdService idService;
  private final FileService filesService;
  private final JsonService jsonService;
  private final Class<T> clazz;

  private boolean containsId(String line, long id) {
    return line.contains("\"id\":" + id + ",");
  }

  @Override
  public long save(T item) {
    try {
      item.setId((long) idService.getIdAndIncrement());
      filesService.appendLineToFile(path, jsonService.toJson(item));

      return item.getId();
    } catch (IOException ex) {
      throw new RuntimeException("Database failed to save invoice", ex);
    }
  }

  @Override
  public Optional<T> getById(long id) {
    try {
      return filesService.readAllLines(path)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, clazz))
          .findFirst();
    } catch (IOException e) {
      throw new RuntimeException("Database failed to get invoice with id: " + id, e);
    }
  }

  @Override
  public List<T> getAll() {
    try {
      return filesService.readAllLines(path)
          .stream()
          .map(line -> jsonService.toObject(line, clazz))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read invoices", e);
    }
  }

  @Override
  public void update(long id, T invoice) {
    try {
      List<String> invoices = filesService.readAllLines(path);
      List<String> listWithoutInvoice = invoices
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());

      if (invoices.size() == listWithoutInvoice.size()) {
        throw new IllegalArgumentException("Invoice with id: " + id + " does not exist in database");
      }

      invoice.setId(id);
      listWithoutInvoice.add(jsonService.toJson(invoice));

      filesService.writeToFile(path, String.join(",", listWithoutInvoice));
    } catch (IOException e) {
      throw new RuntimeException("Failed to update " + id, e);
    }
  }

  @Override
  public void delete(long id) {
    try {
      List<String> updateList = filesService.readAllLines(path)
          .stream()
          .filter(line -> !containsId(line, id))
          .collect(Collectors.toList());

      filesService.writeLinesToFile(path, updateList);

    } catch (IOException e) {
      throw new RuntimeException("Failed to delete invoice " + id, e);
    }
  }
}
