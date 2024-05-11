# Spring Books API

This project is a Spring Boot application for managing books

## Requirements

- Java Development Kit (JDK) 17 or higher.
- Apache Maven for dependency management.

## Technologies Used

- **Spring Boot**: Framework for building Java applications
- **Spring Data JPA**: Part of the larger Spring Data framework, provides easy data access and persistence
- **Spring Security**: Authentication and access control framework
- **Spring Web**: For building web applications with Spring
- **JSON Web Token (JWT)**: For secure authentication and authorization
- **Springdoc OpenAPI**: For generating API documentation with Swagger UI
- **MapStruct**: For object mapping
- **H2 Database**: Lightweight, in-memory database for development and testing
- **Lombok**: Library for reducing boilerplate code in Java

## Installation

1. Clone this repository to your local machine:

```bash
git clone https://github.com/jeremw264/books-api-spring.git
```

2. Navigate to the project directory: :

```bash
cd books-api-spring/
```

3. Compile the project using Maven: :

```bash
mvn clean install
```

4. Run the application using Maven :

```bash
mvn spring-boot:run
```

The application will be available at [http://localhost:3001](http://localhost:3001).

## Swagger Documentation

The API documentation is available via Swagger at http://localhost:3001/api/v1/swagger-ui/index.html#/

## Default Credentials

When using the application, you can utilize the following default credentials to access :

- Username: root
- Password: toor