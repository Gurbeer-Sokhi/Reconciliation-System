package infrrd.assessment.infrrd.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "Invoices")
public class Invoice {
    @Id
    private String id;
    private String purchaseOrderNumber;
    private Date date;
    private List<Item> items;
}