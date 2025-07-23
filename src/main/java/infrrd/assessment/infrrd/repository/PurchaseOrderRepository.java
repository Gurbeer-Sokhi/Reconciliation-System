package infrrd.assessment.infrrd.repository;

import infrrd.assessment.infrrd.model.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String> {
    PurchaseOrder findByPurchaseOrderNumber(String purchaseOrderNumber);
}
