# Banking Solution
A simple REST API for a banking application, built with Java and Spring Boot. The API supports user management (with JWT authentication), account operations (create, list, get details), and basic transactions (deposit, withdraw, transfer). The project uses Liquibase for database migrations and is covered by unit tests.

## Tech Stack
Java 21+
Spring Boot
Spring Data JPA
Liquibase
Maven

## Build and Run

### Build the project
```
.\mvnw clean package
```

### Build without running tests
```
.\mvnw clean package -DskipTests
```

### Run the application
```
.\mvnw spring-boot:run -D"spring-boot.run.profiles"=local
```

The application will start on http://localhost:8080.