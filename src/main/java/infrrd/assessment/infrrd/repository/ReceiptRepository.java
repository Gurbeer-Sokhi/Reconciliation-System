package infrrd.assessment.infrrd.repository;

import infrrd.assessment.infrrd.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReceiptRepository extends MongoRepository<Receipt, String> {
    List<Receipt> findByPurchaseOrderNumber(String purchaseOrderNumber);
}
