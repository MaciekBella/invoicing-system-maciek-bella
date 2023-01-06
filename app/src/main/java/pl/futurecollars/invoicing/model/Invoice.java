package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Invoice {

  @ApiModelProperty(value = "invoice id -> generated", required = true, example = "1")
  private long id;

  @ApiModelProperty(value = "invoice date -> generated", required = true, example = "20-12-2022")
  private LocalDate date;

  @ApiModelProperty(value = "invoice buyer who buy product|services", required = true)
  private Company buyer;

  @ApiModelProperty(value = "invoice company who is selling the product|services", required = true)
  private Company seller;

  @ApiModelProperty(value = "list of products|services", required = true)
  private List<InvoiceEntry> entries;

  public Invoice(LocalDate date, Company buyer, Company seller, List<InvoiceEntry> entries) {
    this.date = date;
    this.buyer = buyer;
    this.seller = seller;
    this.entries = entries;
  }
}
