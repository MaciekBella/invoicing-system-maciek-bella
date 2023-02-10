package pl.futurecollars.invoicing.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

@AllArgsConstructor
public class SqlDatabase implements DataBase {

  private final JdbcTemplate jdbcTemplate;
  private final Map<Vat, Integer> vatToId = new HashMap<>();
  private final Map<Integer, Vat> idToVat = new HashMap<>();

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
        PreparedStatement ps = connection.prepareStatement("insert into invoice_entry (description, price, vat_value, vat_rate) values (?, ?, ?, ?);",
            new String[] {"id"});
        ps.setString(1, entry.getDescription());
        ps.setBigDecimal(2, entry.getPrice());
        ps.setBigDecimal(3, entry.getVatValue());
        ps.setInt(4, vatToId.get(entry.getVatRate()));
        return ps;
      }, keyHolder);

      long invoiceEntryId = keyHolder.getKey().longValue();

      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement("insert into invoice_invoice_entry (invoice_id, invoice_entry_id) values (?, ?);");
        ps.setLong(1, invoiceId);
        ps.setLong(2, invoiceEntryId);
        return ps;
      });
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
  public List<Invoice> getAll () {
    return jdbcTemplate.query( "select i.id, i.date, i.number, c1.name as seller_name, c2.name as buyer_name, c1.taxIdentificationNumber as seller_nip from invoice i "
            + "inner join company c1 on i.seller = c1.id "
            + "inner join company c2 on i.buyer = c2.id",
        (rs, rowNr) -> {
      long invoiceId = rs.getLong( "id");

      List<InvoiceEntry> invoiceEntries = jdbcTemplate.query(
          "select * from invoice_invoice_entry iie "
              + "inner join invoice_entry e on iie.invoice_entry_id = e.id "
              + "where invoice_id = " + invoiceId,
      (response, ignored) -> InvoiceEntry.builder()
          .id(response.getLong( "id"))
          .description(response.getString( "description"))
          .price(response.getBigDecimal( "price"))
          .vatValue(response.getBigDecimal( "vat_value"))
          .vatRate(idToVat.get(response.getInt( "vat_rate")))
          .build());

      return Invoice.builder()
          .id(rs.getLong("id"))
          .date(rs.getDate( "date").toLocalDate())
          .number(rs.getString("number"))
          .buyer(Company.builder().name(rs.getString( "buyer_name")).build())
          .seller(Company.builder().name(rs.getString( "seller_name")).taxIdentificationNumber(rs.getString("seller_nip")).build())
          .entries(invoiceEntries)
          .build();
    });
  }

  @Override
  public void update(long id, Invoice invoice) {

  }

  @Override
  public void delete(long id) {

  }
}
