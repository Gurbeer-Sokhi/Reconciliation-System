package infrrd.assessment.infrrd.repository;

import infrrd.assessment.infrrd.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByPurchaseOrderNumber(String purchaseOrderNumber);
}
