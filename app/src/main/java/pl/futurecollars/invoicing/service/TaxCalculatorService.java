package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.DataBase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

  private final DataBase dataBase;

  public BigDecimal income(String taxIdentificationNumber) {
    return visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal costs(String taxIdentificationNumber) {
    return visit(buyerPredicate(taxIdentificationNumber), InvoiceEntry::getPrice);
  }

  public BigDecimal incomingVat(String taxIdentificationNumber) {
    return visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal outgoingVat(String taxIdentificationNumber) {
    return visit(buyerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
  }

  public BigDecimal getEarnings(String taxIdentificationNumber) {
    return income(taxIdentificationNumber).subtract(costs(taxIdentificationNumber));
  }

  public BigDecimal getVatToReturn(String taxIdentificationNumber) {
    return incomingVat(taxIdentificationNumber).subtract(outgoingVat(taxIdentificationNumber));
  }

  public TaxCalculatorResult calculateTaxes(String taxIdentificationNumber) {
    return TaxCalculatorResult.builder()
        .income(income(taxIdentificationNumber))
        .costs(costs(taxIdentificationNumber))
        .earnings(getEarnings(taxIdentificationNumber))
        .incomingVat(incomingVat(taxIdentificationNumber))
        .outgoingVat(outgoingVat(taxIdentificationNumber))
        .vatToReturn(getVatToReturn(taxIdentificationNumber))
        .build();
  }

  private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
    return invoice -> taxIdentificationNumber.equals(invoice.getSeller().getTaxIdentificationNumber());
  }

  private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
    return invoice -> taxIdentificationNumber.equals(invoice.getBuyer().getTaxIdentificationNumber());
  }

  private BigDecimal visit(Predicate<Invoice> invoicePredicate, Function<InvoiceEntry, BigDecimal> invoiceEntryToValue) {
    return dataBase.getAll()
        .stream()
        .filter(invoicePredicate)
        .flatMap(i -> i.getEntries().stream())
        .map(invoiceEntryToValue)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
