package pl.futurecollars.invoicing.db.jpa;

import org.springframework.data.repository.CrudRepository;
import pl.futurecollars.invoicing.model.Company;

public interface CompanyRepository extends CrudRepository<Company, Long> {
}
