package pl.futurecollars.invoicing.db;

import org.springframework.data.repository.CrudRepository;
import pl.futurecollars.invoicing.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

}
