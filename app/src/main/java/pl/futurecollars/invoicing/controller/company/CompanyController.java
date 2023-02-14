package pl.futurecollars.invoicing.controller.company;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.api.CompanyApi;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.CompanyService;

@RestController
@AllArgsConstructor
@RequestMapping("companies")
public class CompanyController implements CompanyApi {

  private final CompanyService companyService;

  @Override
  public long add(Company company) {
    return companyService.save(company);
  }

  @Override
  public List<Company> getAll() {
    return companyService.getAll();
  }

  @Override
  public Optional<Company> getById(long id) {
    return companyService.getById(id);
  }

  @Override
  public void update(long id, Company company) {
    companyService.update(id, company);
  }

  @Override
  public void delete(long id) {
    companyService.delete(id);
  }
}
