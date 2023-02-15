package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.Company;

@Service
public class CompanyService {

  private final DataBase<Company> database;

  public CompanyService(DataBase<Company> database) {
    this.database = database;
  }

  public long save(Company company) {
    return database.save(company);
  }

  public Optional<Company> getById(long id) {
    return database.getById(id);
  }

  public List<Company> getAll() {
    return database.getAll();
  }

  public void update(long id, Company updatedCompany) {
    database.update(id, updatedCompany);
  }

  public void delete(long id) {
    database.delete(id);
  }

}
