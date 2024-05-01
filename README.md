# Money Transfer Application

## Overview

The **Money Transfer Application** provides a platform for transferring money between accounts, 
supporting multiple currencies and real-time currency conversion. 
It is designed with extensive testing to ensure accurate and efficient operations.

## Key Features
* **Money Transfer:** Transfer money between accounts with automatic currency conversion.
* **Advanced Error Handling:** Custom exception handling for robust error management
and reporting back to user.
* **Multi-Database Compatibility:** Uses PostgreSQL for production and H2 for testing, ensuring reliability and ease of testing.

## Technologies
* **Spring Boot**
* **PostgreSQL & H2 Database:** PostgreSQL for production and H2 for testing.
* **Liquibase:** Manages database schema changes efficiently.
* **Docker:** Simplifies deployment.
* **Groovy Testing:** For meaningful unit testing achieving 100% (branch & instruction) coverage and integration tests to help with the development and scaling.
* **Lombok:** Minimizes boilerplate code.
* **Aspect-Oriented Programming (AOP):** For centralized and efficient logging management.

## Project Structure

```
com.money.transfer.app
│
├── annotation - Contains AOP logic for cross-cutting concerns like logging.
├── configuration - Contains configuration of beans.
├── controller.v1 - REST controller that provides an HTTP endpoint to transfer money.
├── dto - Data Transfer Objects that encapsulate request and response data.
├── entity - Domain models representing database tables.
├── exception - Custom exception handling classes that manage specific error scenarios and a global exception handler.
├── integration - A web client responsible for integrating with an external API.
├── repository - Spring Data JPA repositories for handling CRUD operations on database entities.
├── service - Services that contain the core business logic of the application.
└── utils - Utility class and constants.
```

## Testing

### Groovy Integration Tests

The [MoneyTransferControllerImplIS](src/test/groovy/com/money/transfer/app/controller/v1/MoneyTransferControllerImplIS.groovy)
simulates application behaviour against real Postman requests. It integrates with an H2 database, 
used solely for testing purposes. 

###### _It is standalone. No need to start the production db with the docker-compose._

Also the [ExchangeRateRestClientIS](src/test/groovy/com/money/transfer/app/integration/ExchangeRateRestClientIS.groovy)
validates integration with [external currency rate services](https://app.exchangerate-api.com/).

### Groovy Unit Tests

There are multiple unit tests, but the more meaningful ones business-wise are the following:
* The [MoneyTransferServiceSpec](src/test/groovy/com/money/transfer/app/service/MoneyTransferServiceImplSpec.groovy) which tests the core
bussiness logic of the application, by mocking all the injected components and focusing on the specific service,
* and the [GlobalExceptionHandlerSpec](src/test/groovy/com/money/transfer/app/exception/GlobalExceptionHandlerSpec.groovy)
which sets a standard on how the application handles all exceptions thrown by any controller.

* Also you can run `mvn clean test` and then open the [jacoco.html](target/site/jacoco/index.html) on the browser to assess
the application's unit test coverage.

### Postman Requests

In this case you will first need to start the db with:

`docker compose up`,

then run the application **using your IDE preferably** or by:

`mvn clean install`

`mvn spring-boot:run`

There are two accounts with standard names for testing purposes: **KANE987** and **JAX042**, so that one can begin testing, without
manually interfering with the dB, to add test accounts or retrieve uuids.


## Author
### Evangelos Chaniadakis

Contact: echaniadakis@gmail.com

GitHub: [xaniadakis](https://github.com/xaniadakis)

LinkedIn: [Evangelos Chaniadakis](https://www.linkedin.com/in/evangelos-chaniadakis/)
