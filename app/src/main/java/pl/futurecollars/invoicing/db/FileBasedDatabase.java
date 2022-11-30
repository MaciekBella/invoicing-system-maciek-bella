package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Generated;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Generated
@AllArgsConstructor
public class FileBasedDatabase implements DataBase {

  private final Path path;
  private final IdService idService;
  private final FileService filesService;
  private final JsonService jsonService;

  private boolean containsId(String line, long id) {
    return line.contains("\"id\":" + id + ",");
  }

  @Override
  public long save(Invoice invoice) {
    try {
      invoice.setId(idService.getIdAndIncrement());
      filesService.appendLineToFile(path, jsonService.toJson(invoice));

      return invoice.getId();
    } catch (IOException ex) {
      throw new RuntimeException("Database failed to save invoice", ex);
    }
  }

  @Override
  public Optional<Invoice> getById(long id) {
    try {
      return filesService.readAllLines(path)
          .stream()
          .filter(line -> containsId(line, id))
          .map(line -> jsonService.toObject(line, Invoice.class))
          .findFirst();
    } catch (IOException e) {
      throw new RuntimeException("Database failed to get invoice with id: " + id, e);
    }
  }

  @Override
  public List<Invoice> getAll() {
    try {
      return filesService.readAllLines(path)
          .stream()
          .map(line -> jsonService.toObject(line, Invoice.class))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read invoices", e);
    }
  }

  @Override
  public void update(long id, Invoice invoice) {
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
