package infrrd.assessment.infrrd.service;

import infrrd.assessment.infrrd.model.*;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.*;


@Service
public class ReconciliationService {
    private final MongoTemplate mongoTemplate;

    public ReconciliationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void reconcile(String purchaseOrderNumber) {
        // Fetch PO
        PurchaseOrder po = mongoTemplate.findOne(
                Query.query(Criteria.where("purchaseOrderNumber").is(purchaseOrderNumber)),
                PurchaseOrder.class
                                                );

        if(po == null) {
            System.err.println("PO not found: " + purchaseOrderNumber);
            return;
        }

        // Fetch all receipts
        List<Receipt> receipts = mongoTemplate.find(
                Query.query(Criteria.where("purchaseOrderNumber").is(purchaseOrderNumber)),
                Receipt.class
                                                   );

        // Fetch all invoices
        List<Invoice> invoices = mongoTemplate.find(
                Query.query(Criteria.where("purchaseOrderNumber").is(purchaseOrderNumber)),
                Invoice.class
                                                   );

        // Calculate totals (using simpler approach)
        Map<String, Integer> ordered = new HashMap<>();
        for (Item item : po.getItems()) {
            ordered.put(item.getName(), item.getQuantity());
        }

        Map<String, Integer> received = new HashMap<>();
        for (Receipt r : receipts) {
            for (Item item : r.getItems()) {
                received.merge(item.getName(), item.getQuantity(), Integer::sum);
            }
        }

        Map<String, Integer> invoiced = new HashMap<>();
        for (Invoice i : invoices) {
            for (Item item : i.getItems()) {
                invoiced.merge(item.getName(), item.getQuantity(), Integer::sum);
            }
        }

        // Check discrepancies
        System.out.println("\n=== Reconciling PO: " + purchaseOrderNumber + " ===");

        // Check ordered vs received
        ordered.forEach((item, qty) -> {
            int receivedQty = received.getOrDefault(item, 0);
            if(receivedQty < qty) {
                System.out.printf("UNDER-DELIVERY: %s (Ordered: %d, Received: %d)%n",
                                  item, qty, receivedQty);
            } else if(receivedQty > qty) {
                System.out.printf("OVER-DELIVERY: %s (Ordered: %d, Received: %d)%n",
                                  item, qty, receivedQty);
            }
        });

        // Check received vs invoiced
        received.forEach((item, qty) -> {
            int invoicedQty = invoiced.getOrDefault(item, 0);
            if(invoicedQty < qty) {
                System.out.printf("UNDER-INVOICED: %s (Received: %d, Invoiced: %d)%n",
                                  item, qty, invoicedQty);
            } else if(invoicedQty > qty) {
                System.out.printf("OVER-INVOICED: %s (Received: %d, Invoiced: %d)%n",
                                  item, qty, invoicedQty);
            }
        });

        // Check for missing deliveries
        ordered.keySet().stream()
               .filter(item -> !received.containsKey(item))
               .forEach(item -> System.out.printf("MISSING DELIVERY: %s (Ordered: %d)%n",
                                                  item, ordered.get(item)));

        // Check for extra items
        received.keySet().stream()
                .filter(item -> !ordered.containsKey(item))
                .forEach(item -> System.out.printf("EXTRA ITEM DELIVERED: %s (Quantity: %d)%n",
                                                   item, received.get(item)));

        System.out.println("=== Reconciliation complete ===\n");
    }
}
