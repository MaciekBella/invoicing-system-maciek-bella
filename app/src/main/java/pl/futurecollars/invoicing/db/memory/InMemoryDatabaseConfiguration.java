package pl.futurecollars.invoicing.db.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
public class InMemoryDatabaseConfiguration {

  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
  @Bean
  public DataBase<Invoice> inMemoryDataBase() {
    log.debug("Creating in-memory database");
    return new InMemoryDatabase<>();
  }

  @Bean
  public DataBase<Company> companyInMemoryDatabase() {
    return new InMemoryDatabase<>();
  }
}
