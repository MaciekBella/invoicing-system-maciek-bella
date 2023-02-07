package pl.futurecollars.invoicing.model;

import static javax.persistence.CascadeType.ALL;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "invoice id -> generated", required = true, example = "1")
  private long id;

  @ApiModelProperty(value = "invoice date -> generated", required = true, example = "2022-12-08")
  private LocalDate date;

  @JoinColumn(name = "buyer")
  @OneToOne(cascade = ALL)
  @ApiModelProperty(value = "invoice buyer who buy product|services", required = true)
  private Company buyer;

  @JoinColumn(name = "seller")
  @OneToOne(cascade = ALL)
  @ApiModelProperty(value = "invoice company who is selling the product|services", required = true)
  private Company seller;

  @JoinColumn(name = "invoice_invoice_entry")
  @OneToMany(cascade = ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @ApiModelProperty(value = "list of products|services", required = true)
  private List<InvoiceEntry> entries;

  public Invoice(LocalDate date, Company buyer, Company seller, List<InvoiceEntry> entries) {
    this.date = date;
    this.buyer = buyer;
    this.seller = seller;
    this.entries = entries;
  }
}
