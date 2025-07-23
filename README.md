# Reconciliation-System

## High level design

![Reconciliation Design](https://github.com/user-attachments/assets/b93663ad-9198-4519-9099-d9d4421e5552)







## Steps to Run and Test the project

### Step 1: Clone this repository

### Step 2: Setup MongoDB
  - Go into the mongoDB folder and run:
           ``` 
           docker compose up -d 
           ```
  - Once the container is up, run:
          ``` 
          docker exec -it mongo-single mongosh 
          ```
  - Once in mongosh, run:
          
          ``` 
          rs.initiate({
              _id: "rs0",
              members: [{ _id: 0, host: "localhost:27017" }]
              })
          ```
          
### Step 3: Test Cases (Mongosh Commands):
- Scenario 1 - Under-delivery
          
          ```
          db.PurchaseOrders.insertOne({
          purchaseOrderNumber: "PO-001",
          vendor: "ABC",
          items: [
          {name: "Chair", quantity: 50},
          {name: "Table", quantity: 40}
          ]
          })
          ```
          
          ``` 
          db.Receipts.insertOne({
          purchaseOrderNumber: "PO-001",
          items: [
          {name: "Chair", quantity: 40}
          ]
          })
          ```
- Scenario 2 Over-Invoicing
          
          ```
          db.Receipts.insertOne({
          purchaseOrderNumber: "PO-001",
          items: [
          {name: "Table", quantity: 40}
          ]
          })
          ```
          
          ```
          db.Invoices.insertOne({
          purchaseOrderNumber: "PO-001",
          items: [
          {name: "Chair", quantity: 45},
          {name: "Table", quantity: 40}
          ]
          })
          ```

- Scenario 3 Under-Invoicing
          
          ```
          db.Receipts.insertOne({
          purchaseOrderNumber: "PO-002",
          items: [
          {name: "Microscope", quantity: 10}
          ]
          })
          ```
          
          ```
          db.Invoices.insertOne({
          purchaseOrderNumber: "PO-002",
          items: [
          {name: "Microscope", quantity: 8}
          ]
          })
          ```


