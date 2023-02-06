package pl.futurecollars.invoicing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Company id (generated by application)", required = true, example = "1")
  private Long id;

  @ApiModelProperty(value = "Tax identification number", required = true, example = "552-168-00")
  private String taxIdentificationNumber;

  @ApiModelProperty(value = "Company address", required = true, example = "ul.Mazowiecka 134, 32-525 Radzionkow")
  private String address;

  @ApiModelProperty(value = "Company name", required = true, example = "Invoice House Ltd.")
  private String name;

  public Company(String taxIdentificationNumber, String address, String name) {
    this.taxIdentificationNumber = taxIdentificationNumber;
    this.address = address;
    this.name = name;
  }

}
