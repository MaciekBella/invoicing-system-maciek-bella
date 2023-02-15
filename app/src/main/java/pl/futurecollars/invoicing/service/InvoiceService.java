package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.Invoice;

@Service
public class InvoiceService {

  private final DataBase<Invoice> dataBase;

  @Autowired
  public InvoiceService(DataBase<Invoice> database) {
    this.dataBase = database;
  }

  public long save(Invoice invoice) {
    return dataBase.save(invoice);
  }

  public Optional<Invoice> getById(long id) {
    return dataBase.getById(id);
  }

  public List<Invoice> getAll() {
    return dataBase.getAll();
  }

  public void update(long id, Invoice updatedInvoice) {
    updatedInvoice.setId(id);
    dataBase.update(id, updatedInvoice);
  }

  public void delete(long id) {
    dataBase.delete(id);
  }

}
