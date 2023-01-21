package pl.futurecollars.invoicing.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@Repository
@AllArgsConstructor
public class SqlDatabase implements DataBase {

  private JdbcTemplate jdbcTemplate;
  private Map<Vat, Integer> vatToId = new HashMap<>();

  @PostConstruct
  void initVat() {
    jdbcTemplate.query("select * from vat",
        rs -> {
          Vat vat = Vat.valueOf("VAT_" + rs.getString("name"));
          int id = rs.getInt("id");
          vatToId.put(vat, id);
        });
  }

  @Override
  public long save(Invoice invoice) {
    long buyerId = insertCompany(invoice.getBuyer());
    long sellerId = insertCompany(invoice.getSeller());

    long invoiceId = insertInvoice(invoice, buyerId, sellerId);

    addEntries(invoice.getEntries(), invoiceId);

    return invoiceId;
  }

  private void addEntries(List<InvoiceEntry> invoiceEntries, long invoiceId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    invoiceEntries.forEach(entry -> {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("insert into invoice_entry (description, price, vatValue, vatRate) values (?, ?, ?, ?);",
            new String[] {"id"});
        ps.setString(1, entry.getDescription());
        ps.setBigDecimal(2, entry.getPrice());
        ps.setBigDecimal(3, entry.getVatValue());
        ps.setInt(4, vatToId.get(entry.getVatRate()));
        return ps;
      }, keyHolder);

      long invoiceEntryId = keyHolder.getKey().longValue();

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);",
            new String[] {"id"});
        ps.setLong(1, invoiceId);
        ps.setLong(2, invoiceEntryId);
        return ps;
      });
    });
  }

  private long insertInvoice(Invoice invoice, long buyerId, long sellerId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into invoice (date, buyer, seller) values (?, ?, ?);",
          new String[] {"id"});
      ps.setDate(1, Date.valueOf(invoice.getDate()));
      ps.setLong(2, buyerId);
      ps.setLong(3, sellerId);
      return ps;
    }, keyHolder);

    return keyHolder.getKey().longValue();
  }

  private long insertCompany(Company company) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into company (name, address, taxIdentificationNumber) values (?, ?, ?);",
          new String[] {"id"});
      ps.setString(1, company.getName());
      ps.setString(2, company.getAddress());
      ps.setString(3, company.getTaxIdentificationNumber());
      return ps;
    }, keyHolder);

    return keyHolder.getKey().longValue();
  }

  @Override
  public Optional<Invoice> getById(long id) {
    return Optional.empty();
  }

  @Override
  public List<Invoice> getAll() {
    return null;
  }

  @Override
  public void update(long id, Invoice invoice) {

  }

  @Override
  public void delete(long id) {

  }
}
