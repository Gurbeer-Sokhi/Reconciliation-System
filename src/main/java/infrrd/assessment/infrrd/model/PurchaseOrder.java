package infrrd.assessment.infrrd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import lombok.Data;


@Data
@Document(collection = "PurchaseOrders")
public class PurchaseOrder {
    @Id
    private String id;
    private String purchaseOrderNumber;
    private String vendor;
    private List<Item> items;
}