# SmartCalc - Database

## 1. Overview

This database is used to store the calculation history of our SmartCalc website. It saves the operation, expression, result, and date and time of each calculation so users can view their previous calculations.

## 2. Technologies Used

- **MySQL** - Stores the calculation history.
- **Spring Data JPA** - Helps the backend interact with the database.
- **Hibernate** - Maps Java objects to database tables and manages database operations.
- **MySQL Connector/J** - Connects the Spring Boot backend to MySQL.

## 3. Database Design

We use one table called `calculation_history`. It stores different types of calculations, such as addition, subtraction, multiplication, division, and scientific calculations.

| **Column** | **Data Type** | **Description** |
|---|---|---|
| `id` | BIGINT | Unique ID for each record. It is automatically generated and acts as the primary key. |
| `operation` | VARCHAR(80) | Type of calculation, such as Addition or SIN. |
| `expression` | VARCHAR(500) | The calculation expression entered by the user, such as `5 + 3`. |
| `result` | VARCHAR(1000) | The final answer of the calculation. |
| `created_at` | TIMESTAMP | Date and time when the calculation was recorded. |

All columns are required and cannot contain `NULL` values.

### SQL to Create the Database and Table

```sql
CREATE DATABASE IF NOT EXISTS smartcalc;

USE smartcalc;

CREATE TABLE IF NOT EXISTS calculation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation VARCHAR(80) NOT NULL,
    expression VARCHAR(500) NOT NULL,
    result VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## 4. Database Operations

- **Save calculation** - Saves a calculation after it is performed.
- **View history** - Retrieves previous calculations so users can view them again.
- **Delete calculation** - Deletes a particular record using its ID.

In the first version, there are no user accounts, so the calculation history is shared among all users.

## 5. Backend Integration

The backend uses Spring Boot to connect the website with the MySQL database. Spring Data JPA and Hibernate help the backend manage calculation records.

The `CalculationHistory` Java entity maps to the `calculation_history` table.

The backend is expected to provide the following API endpoints:

| **Method** | **Endpoint** | **Purpose** |
|---|---|---|
| POST | `/api/calculator/calculate` | Performs a calculation and saves the result. |
| GET | `/api/calculator/history` | Retrieves previous calculations. |
| DELETE | `/api/calculator/history/{id}` | Deletes a record using its ID. |

## 6. Notes

- Database connection settings are maintained in the backend's `application.properties` file.
- Database usernames and passwords should not be shared publicly.
- The `schema.sql` file contains the SQL commands used to create the database and table.
- Hibernate and the backend entity should remain consistent with the database structure.
- The database design may change in the future as new features are added.