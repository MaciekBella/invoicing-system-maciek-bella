package pl.futurecollars.invoicing.controller.tax;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.api.TaxCalculatorApi;
import pl.futurecollars.invoicing.service.TaxCalculatorResult;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@RestController
@AllArgsConstructor
public class TaxCalculatorController implements TaxCalculatorApi {

  private TaxCalculatorService taxCalculatorService;

  @Override
  public TaxCalculatorResult calculateTaxes(String taxIdentificationNumber) {
    return taxCalculatorService.calculateTaxes(taxIdentificationNumber);
  }
}
