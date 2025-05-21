# Project Introduction

This is a pharmaceutical supply chain and prescription fulfillment system. It manages the basic information and inventory of drugs, pharmacies, and prescriptions, as well as the handling of patient prescriptions and various related log information.

# Environment Requirements

Java Version: JDK 21

Build Tool: Maven

Spring Boot Version: 3.1.8


# Installation and Running Instructions

Install dependencies and build the project:

`mvn clean install`

Start the application:

`mvn spring-boot:run`

# Local Database Setup
To run PostgreSQL locally via Docker, use the following command with the provided docker-compose.yml file:

`docker-compose up`

# API Documentation


## Drug API

**Create a Drug:**

URL：`POST /drugs`

Request Body:
```
{
  "name": "Paracetamol",
  "manufacturer": "ABC Pharma",
  "batchNumber": "B1234",
  "expiryDate": "2025-12-31",
  "stock": 100
}

```

Response Status: `200 OK`

Response Body:
```
{
    "id": 101,
    "name": "Paracetamol",
    "manufacturer": "ABC Pharma",
    "batchNumber": "B1234",
    "expiryDate": "2025-12-31",
    "stock": 100
}
```

**Update Drug Stock/Information:**

URL：`PUT /drugs/{id}`

Path Parameter: `id`

Request Body:
```
{
  "addedStock": 50
}

```

Response Status: `200 OK`

Response Body:
```
{
    "id": 101,
    "name": "Paracetamol",
    "manufacturer": "ABC Pharma",
    "batchNumber": "B1234",
    "expiryDate": "2025-12-31",
    "stock": 100
}
```

## Pharmacy API

**Get Pharmacy and Drug Information:**

URL：`GET /pharmacies`

Response Status：`200 OK`

Response Body:
```
[
  {
    "pharmaciesId": 201,
    "name": "Central Pharmacy",
    "address": "123 Main St, Springfield",
    "phone": "+1 234 567 890",
    "drugInfoList": [
      {
        "id": 101,
        "name": "Paracetamol",
        "manufacturer": "ABC Pharma",
        "batchNumber": "B1234",
        "expiryDate": "2025-12-31",
        "maxAllocationAmount": 500,
        "dispensingAmount": 450
      },
      {
        "id": 102,
        "name": "Ibuprofen",
        "manufacturer": "XYZ Labs",
        "batchNumber": "C5678",
        "expiryDate": "2026-06-15",
        "maxAllocationAmount": 300,
        "dispensingAmount": 250
      }
    ]
  },
  {
    "pharmaciesId": 202,
    "name": "Downtown Pharmacy",
    "address": "456 Elm St, Springfield",
    "phone": "+1 234 678 901",
    "drugInfoList": [
      {
        "id": 103,
        "name": "Amoxicillin",
        "manufacturer": "PharmaCo",
        "batchNumber": "D2345",
        "expiryDate": "2025-11-20",
        "maxAllocationAmount": 200,
        "dispensingAmount": 180
      },
      {
        "id": 104,
        "name": "Aspirin",
        "manufacturer": "MedPlus",
        "batchNumber": "E6789",
        "expiryDate": "2027-05-10",
        "maxAllocationAmount": 1000,
        "dispensingAmount": 900
      }
    ]
  }
]


```

## Prescription API

**Create Prescription:**

URL：`POST /prescriptions`

Request Body:
```
{
    "patientId": 1001,
    "drugId": 501,
    "quantity": 10
}
```

Response Status: `201 Created`

Response Body:
```
{
  "prescriptionId": 1001,
  "prescriptionStatus": "CREATED"
}
```

**Fulfill Prescription:**

URL：`PUT /prescriptions/{id}/status/fulfilled`

Path Parameter:  `id`

Response Status: `200 OK`

Response Body:
```
{
  "prescriptionId": 1001,
  "prescriptionStatus": "FULFILLED"
}
```

## Audit Log API

**Get Audit Log List:**

URL：`GET /audit-logs`

Query Parameters:  `patientId, pharmacyId, status `

Response Status: `200 OK`

Response Body:
```
[
    {
        "id": 1,
        "prescriptionId": 1001,
        "patientId": 501,
        "pharmacyId": 201,
        "status": "APPROVED",
        "errorMessage": null,
        "createdAt": "2025-05-20T14:30:00",
        "drugs": [
            {
                "drugId": 101,
                "quantityRequested": 50,
                "quantityDispensed": 45
            },
            {
                "drugId": 102,
                "quantityRequested": 100,
                "quantityDispensed": 90
            }
        ]
    },
    {
        "id": 2,
        "prescriptionId": 1002,
        "patientId": 502,
        "pharmacyId": 202,
        "status": "REJECTED",
        "errorMessage": "Prescription not valid.",
        "createdAt": "2025-05-21T10:00:00",
        "drugs": [
            {
                "drugId": 103,
                "quantityRequested": 30,
                "quantityDispensed": 30
            }
        ]
    }
]
```

# Test Documentation
**Testing Tools and Frameworks:**

JUnit 5, Mockito, Spring Boot Test, H2

**Test Execution:**
`mvn test`


**Controller Layer Tests (API Testing)**

`AuditControllerTest`

`DrugControllerTest`

`PharmacyControllerTest`

`PrescriptionControllerTest`


**Repository Layer Tests (Data Persistence Testing)**

`AuditLogRepositoryTest`

`DrugRepositoryTest`

`PharmacyDrugInfoRepositoryTest	`

`PharmacyRepositoryTest`

`PrescriptionRepositoryTest`


**Service Layer Tests (Core Business Logic Testing)**

`AuditLogServiceTest`

`DrugServiceTest`

`PharmacyServiceTest`

`PrescriptionServiceTest`

`PrescriptionStatusServiceTest`

`StockUpdateServiceTest`



# Lint Usage Instructions

**Code Style Check (Checkstyle)**

The project integrates the Checkstyle plugin with the src/main/resources/checkstyle.xml rules file.

Check Command:

`mvn checkstyle:check`



# Database Schema Design
![img.png](img.png)