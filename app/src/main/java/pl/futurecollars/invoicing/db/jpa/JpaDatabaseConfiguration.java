package pl.futurecollars.invoicing.db.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
public class JpaDatabaseConfiguration {

  @Bean
  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
  public DataBase<Invoice> jpaDatabase(InvoiceRepository invoiceRepository) {
    log.debug("Creating jpa database");
    return new JpaDatabase<>(invoiceRepository);
  }

  @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
  @Bean
  public DataBase<Company> companyJpaDatabase(CompanyRepository repository) {
    return new JpaDatabase<>(repository);
  }
}
