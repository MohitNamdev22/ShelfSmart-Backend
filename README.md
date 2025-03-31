
# ShelfSmart(Backend)

**ShelfSmart(Backend)** is the server-side component of the ShelfSmart inventory management system, built with Spring Boot. It provides RESTful APIs for inventory management, user authentication, supplier tracking, and AI-driven suggestions, using PostgreSQL as the database.

## Features
- **Authentication**: JWT-based user registration, login, and logout.
- **Inventory Management**: CRUD operations, stock movement tracking, and consumption.
- **Suppliers**: Admin-only supplier management.
- **Reports**: Daily, weekly, and custom CSV reports.
- **User Activity**: Log and retrieve user actions.
- **Alerts**: Low stock and expiry notifications.
- **AI Suggestions**: Inventory restock suggestions via Gemini API.

## Tech Stack
- **Framework**: Spring Boot 3.4.4
- **Language**: Java 17
- **Database**: PostgreSQL (NeonDB)
- **ORM**: Spring Data JPA
- **Security**: Spring Security, JWT
- **Build Tool**: Maven
- **External API**: Gemini API


## Prerequisites
- **Java**: 17
- **Maven**: 3.6+
- **PostgreSQL**: NeonDB or local instance
- **Gemini API Key**

## Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/MohitNamdev22/ShelfSmart-Backend.git
   cd shelfsmart-backend

2. **Set Up Environment Variables**:
Copy data from application.properties.sample & then add a new Gemini API Key

```bash
spring.datasource.url=jdbc:postgresql://<your-neon-db-url>/shelfsmart
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
gemini.api.key=<your-gemini-api-key>
```

3. **Install Dependencies**:
```bash

mvn install
```

4. **Run the Application**:
```bash


mvn spring-boot:run
```
Runs on http://localhost:8080.

5. **Build for Production**:
```bash

mvn clean package
java -jar target/shelfsmart-backend-0.0.1-SNAPSHOT.jar
```


## API Endpoints

### Must-Have (Implemented)

* *   **POST /user/register**: Register user
* *   **POST /user/login**: Login (JWT)
* *   **POST /user/logout**: Logout
* *   **POST /inventory**: Add item (Admin)
* *   **PUT /inventory/{id}**: Edit item (Admin)
* *   **DELETE /inventory/{id}**: Delete item (Admin)
* *   **GET /inventory**: View items
* *   **GET /alerts/expiry**: Expiry alerts
* *   **GET /reports/daily**: Daily report
* *   **GET /reports/weekly**: Weekly report

### Good-to-Have (Implemented)

* *   **GET /inventory/search**: Search items
* *   **GET /reports/custom**: Custom reports
* *   **GET /activity**: User activity log
* *   **GET|POST|PUT|DELETE /suppliers**: Supplier management (Admin)

### Extra Add-ons Feature
* *   **GET /suggestions**: Get Recommendation on Stock Management based on Usage trend using Generative AI


## Usage

* *   Start the backend and connect it to the ShelfSmart Frontend.
* *   Use an Admin account to manage inventory and suppliers.

