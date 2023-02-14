package pl.futurecollars.invoicing.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;


public class SqlDatabase implements DataBase {

  private final JdbcTemplate jdbcTemplate;

  public SqlDatabase(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final Map<Vat, Integer> vatToId = new HashMap<>();
  private final Map<Integer, Vat> idToVat = new HashMap<>();

  private final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();


  @PostConstruct
  void initVat() {
    jdbcTemplate.query("select * from vat",
        rs -> {
          Vat vat = Vat.valueOf("VAT_" + rs.getString("name"));
          int id = rs.getInt("id");
          vatToId.put(vat, id);
          idToVat.put(id, vat);
        });
  }

  @Override
  @Transactional
  public long save(Invoice invoice) {
    long buyerId = insertCompany(invoice.getBuyer());
    long sellerId = insertCompany(invoice.getSeller());
    long invoiceId = insertInvoice(invoice, buyerId, sellerId);
    addEntriesRelatedToInvoice(invoiceId, invoice);
    return invoiceId;
  }

  private long insertInvoiceEntries(InvoiceEntry entry) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection
          .prepareStatement
              ("insert into invoice_entry (description, price, vat_value, vat_rate) values (?, ?, ?, ?);",
                  new String[] {"id"});
      ps.setString(1, entry.getDescription());
      ps.setBigDecimal(2, entry.getPrice());
      ps.setBigDecimal(3, entry.getVatValue());
      ps.setInt(4, vatToId.get(entry.getVatRate()));
      return ps;
    }, keyHolder);
    return Objects.requireNonNull(keyHolder.getKey()).longValue();
  }

  private void insertInvoiceInvoiceEntry(long invoiceId, long invoiceEntryId) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);");
      ps.setLong(1, invoiceId);
      ps.setLong(2, invoiceEntryId);
      return ps;
    });
  }

  private long insertInvoice(Invoice invoice, long buyerId, long sellerId) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement("insert into invoice (date, number, buyer, seller) values (?, ?, ?, ?);",
          new String[] {"id"});
      ps.setDate(1, Date.valueOf(invoice.getDate()));
      ps.setString(2, invoice.getNumber());
      ps.setLong(3, buyerId);
      ps.setLong(4, sellerId);
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
    return jdbcTemplate.query(
        "select i.id, i.date, i.number, c1.name as seller_name, c2.name as buyer_name, c1.taxIdentificationNumber as seller_nip from invoice i "
            + "inner join company c1 on i.seller = c1.id "
            + "inner join company c2 on i.buyer = c2.id",
        (rs, rowNr) -> {
          long invoiceId = rs.getLong("id");

          List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
              "select * from invoice_invoice_entry iie "
                  + "inner join invoice_entry e on iie.invoice_entry_id = e.id "
                  + "where invoice_id = " + invoiceId,
              (response, ignored) -> InvoiceEntry.builder()
                  .id(response.getLong("id"))
                  .description(response.getString("description"))
                  .price(response.getBigDecimal("price"))
                  .vatValue(response.getBigDecimal("vat_value"))
                  .vatRate(idToVat.get(response.getInt("vat_rate")))
                  .build());

          return Invoice.builder()
              .id(rs.getLong("id"))
              .date(rs.getDate("date").toLocalDate())
              .number(rs.getString("number"))
              .buyer(Company.builder()
                  .id(rs.getLong("buyer_id"))
                  .taxIdentificationNumber(rs.getString("buyer_nip"))
                  .name(rs.getString("buyer_name"))
                  .address(rs.getString("buyer_address"))
                  .build())
              .seller(Company.builder()
                  .id(rs.getLong("seller_id"))
                  .taxIdentificationNumber(rs.getString("seller_nip"))
                  .name(rs.getString("seller_name"))
                  .address(rs.getString("seller_address"))
                  .build())
              .invoiceEntries(invoiceEntries)
              .build();
        });
  }

  @Override
  @Transactional
  public void update(long id, Invoice updatedInvoice) {
    Optional<Invoice> originalInvoice = getById(id);
    if (originalInvoice.isPresent()) {
      updateInvoiceData(updatedInvoice, id);
      updateCompany(updatedInvoice.getBuyer(), originalInvoice.get().getBuyer());
      updateCompany(updatedInvoice.getSeller(), originalInvoice.get().getSeller());
      deleteEntriesRelatedToInvoice(id);
      addEntriesRelatedToInvoice(id, updatedInvoice);
    } else {
      throw new RuntimeException("Failed to update invoice with id: " + id);
    }
  }

  private void updateInvoiceData(Invoice updatedInvoice, long originalInvoiceId) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update invoices "
              + "set date=?, "
              + "number=? "
              + "where id=?"
      );
      ps.setDate(1, Date.valueOf(updatedInvoice.getDate()));
      ps.setString(2, updatedInvoice.getNumber());
      ps.setLong(3, originalInvoiceId);
      return ps;
    });
  }

  private void updateCompany(Company updatedCompany, Company originalCompany) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "update companies "
              + "set tax_identification_number=?, "
              + "address=?, "
              + "name=?, "
              + "where id=?"
      );
      ps.setString(1, updatedCompany.getTaxIdentificationNumber());
      ps.setString(2, updatedCompany.getAddress());
      ps.setString(3, updatedCompany.getName());
      ps.setLong(4, originalCompany.getId());
      return ps;
    });
  }

  private void addEntriesRelatedToInvoice(long invoiceId, Invoice invoice) {
    for (InvoiceEntry entry : invoice.getInvoiceEntries()) {
      long invoiceEntryId = insertInvoiceEntries(entry);
      insertInvoiceInvoiceEntry(invoiceId, invoiceEntryId);
    }
  }

  @Override
  @Transactional
  public void delete(long id) {
    Optional<Invoice> invoiceOptional = getById(id);
    if (invoiceOptional.isPresent()) {
      deleteEntriesRelatedToInvoice(id);
      deleteInvoice(id);
      deleteCompaniesRelatedToInvoice(invoiceOptional.get());
    }
  }


  private void deleteEntriesRelatedToInvoice(long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoice_entries where id in (select invoice_entry_id from invoice_invoice_entry where invoice_id=?);");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteInvoice(long id) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from invoices where id = ?;");
      ps.setLong(1, id);
      return ps;
    });
  }

  private void deleteCompaniesRelatedToInvoice(Invoice invoice) {
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "delete from companies where id in (?, ?);");
      ps.setLong(1, invoice.getBuyer().getId());
      ps.setLong(2, invoice.getSeller().getId());
      return ps;
    });
  }
}

