# SmartCalc - Database

## 1. Overview

This database is used to store the calculation history of our SmartCalc website. It saves the calculations a user has done so they can view them again later.

## 2. Technologies Used

- **MySQL** - stores the calculation history.
- **Spring Data JPA** - helps the backend interact with the database.
- **Hibernate** - maps Java objects to database tables and helps manage database operations.
- **MySQL Connector/J** - connects Spring Boot to MySQL.

## 3. Database Design

We use one table called `calculation_history`. It stores different types of calculations, such as addition, subtraction, multiplication, division, and scientific calculations.

| Column | Data Type | Description |
|---|---|---|
| `id` | BIGINT | Unique ID for each record. |
| `operation` | VARCHAR(50) | Type of calculation, such as ADD or SIN. |
| `input_data` | TEXT | The values or expression entered by the user. |
| `result` | VARCHAR(255) | The final answer of the calculation. |
| `created_at` | TIMESTAMP | Date and time when the calculation was recorded. |

### SQL to Create the Table

```sql
CREATE TABLE calculation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation VARCHAR(50) NOT NULL,
    input_data TEXT NOT NULL,
    result VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 4. Database Operations

- **Save calculation** - saves a calculation after it is performed.
- **View history** - retrieves previous calculations so users can view them again.
- **Delete calculation** - deletes a particular record using its ID.

In the first version, there are no user accounts, so the calculation history is shared among all users.

## 5. Backend Integration

The backend uses Spring Boot to connect the website with the MySQL database. Spring Data JPA and Hibernate help the backend manage the calculation records.

The backend provides the following API endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/calculator/calculate` | Performs a calculation and saves the result. |
| GET | `/api/calculator/history` | Retrieves previous calculations. |
| DELETE | `/api/calculator/history/{id}` | Deletes a record using its ID. |

## 6. Notes

- Database connection settings are maintained in the backend's `application.properties` file.
- Database usernames and passwords should not be shared publicly.
- The SQL table definition shows the planned structure of the database.
- The design may change in the future if new features are added.