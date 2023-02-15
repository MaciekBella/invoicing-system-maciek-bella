package pl.futurecollars.invoicing.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.futurecollars.invoicing.model.Company;

@Api(tags = {"company-controller"})
public interface CompanyApi {

  @PostMapping
  @ApiOperation(value = "Add company")
  long add(@RequestBody Company company);

  @GetMapping
  @ApiOperation(value = "Get All companies")
  List<Company> getAllCompanies();

  @GetMapping("/{id}")
  @ApiOperation(value = "Get company by Id")
  Optional<Company> getById(@PathVariable long id);

  @PutMapping("/{id}")
  @ApiOperation(value = "Update company by Id")
  void update(@PathVariable long id, @RequestBody Company company);

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete company by Id")
  void delete(@PathVariable long id);
}

