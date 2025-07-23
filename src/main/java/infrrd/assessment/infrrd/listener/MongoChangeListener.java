package infrrd.assessment.infrrd.listener;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.OperationType;
import infrrd.assessment.infrrd.service.ReconciliationService;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MongoChangeListener implements CommandLineRunner {
    private final MongoClient mongoClient;
    private final ReconciliationService reconciliationService;

    public MongoChangeListener(MongoClient mongoClient,
                               ReconciliationService reconciliationService) {
        this.mongoClient = mongoClient;
        this.reconciliationService = reconciliationService;
    }

    @Override
    public void run(String... args) {
        // Listen to Receipts collection
        new Thread(() -> listen("Receipts")).start();

        // Listen to Invoices collection
        new Thread(() -> listen("Invoices")).start();
    }

    private void listen(String collectionName) {
        MongoDatabase db = mongoClient.getDatabase("infrrd_db");
        MongoCollection<Document> collection = db.getCollection(collectionName);

        while(true) {
            try {
                collection.watch().forEach(change -> {
                    if(change.getOperationType() == OperationType.INSERT) {
                        Document doc = change.getFullDocument();
                        String poNumber = doc.getString("purchaseOrderNumber");
                        System.out.println("New " + collectionName + " for PO: " + poNumber);
                        reconciliationService.reconcile(poNumber);
                    }
                });
            } catch(Exception e) {
                System.err.println("Error in " + collectionName + " listener: " + e.getMessage());
                try {
                    Thread.sleep(5000); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}