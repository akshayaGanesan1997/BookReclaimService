
# Book Marketplace Application

![Java](https://img.shields.io/badge/Java-11-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.4-brightgreen)
![Database](https://img.shields.io/badge/Database-Your%20Database%20Name-blue)

The Book Marketplace Application is a Java-based web application developed using Spring Boot. It provides a platform for buying and selling books across various categories. Users can register, list books for sale, search for books, and perform transactions.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Error Handling](#error-handling)
- [Contributing](#contributing)
- [License](#license)

## Features

- **User Registration and Authentication**: Users can create accounts and log in securely.

- **Listing Books for Sale**: Sellers can list their books for sale with detailed information, including title, author, price, and condition.

- **Search for Books**: Users can search for books by various criteria, including category, title, author, ISBN, and keyword.

- **Buy and Sell Books**: The platform facilitates buying and selling books through user-friendly transactions.

- **User Funds Management**: Users can manage their funds for buying books.

- **Validation and Error Handling**: Comprehensive validation and error handling for a smooth user experience.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java 11**: You should have Java 11 or later installed.

- **Spring Boot 2.5.4**: This project is built using Spring Boot, version 2.5.4.

- **Database**: You need a database for storing book and user information. You can configure the database connection in the `application.properties` file.

- **Maven**: Maven is used for building and managing dependencies.

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/BookMarketplace.git
   ```

2. Navigate to the project directory:

   ```bash
   cd BookMarketplace
   ```

3. Build the project:

   ```bash
   mvn clean install
   ```

4. Run the application:

   ```bash
   java -jar target/BookMarketPlaceApplication.jar
   ```

## Usage

- Access the application in your web browser at `http://localhost:8080`.

## API Endpoints

This application exposes a set of RESTful API endpoints for various operations. The following are some of the key endpoints:

### Books

- `GET /api/books`: Get all books.
- `GET /api/books/getAvailableBooks`: Get available books.
- `GET /api/books/getBookById`: Get a book by ID.
- `GET /api/books/getBooksByCategory`: Get books by category.
- `GET /api/books/getBookByISBN`: Get a book by ISBN.
- `GET /api/books/searchBooks`: Search for books by keyword.
- `POST /api/books/addBook`: Add a new book for sale.
- `PUT /api/books/updateBook`: Update book details.
- `DELETE /api/books/deleteBook`: Delete a book.

### Users

- `GET /api/users`: Get all users.
- `GET /api/users/getUserById`: Get a user by ID.
- `POST /api/users/addUser`: Add a new user.
- `PUT /api/users/updateUser`: Update user details.
- `DELETE /api/users/deleteUser`: Delete a user.

### Transactions

- `GET /api/transactions`: Get all transactions.
- `GET /api/transactions/getTransactionById`: Get a transaction by ID.
- `POST /api/transactions/addTransaction`: Add a new transaction.
- `PUT /api/transactions/updateTransaction`: Update transaction details.
- `DELETE /api/transactions/deleteTransaction`: Delete a transaction.

## API Documentation

The API is documented using Swagger, which provides interactive documentation for all endpoints in swagger.yml file.

## Database

This application uses a database to store book and user information. You can configure the database connection in the `application.properties` file.

## Error Handling

Errors in the Book Marketplace Application are handled using a global exception handler, `GlobalExceptionHandler`. This component provides customized responses for specific error scenarios.

### Validation Errors

Validation errors are handled using the `ValidationException` class. When a validation error occurs, the `handleValidationException` method in `GlobalExceptionHandler` is triggered. It returns a `400 Bad Request` response with details about the validation errors.

### Invalid Enum Values

Invalid enum values are handled using the `InvalidEnumException` class. In cases where an invalid enum value is provided in a request, the `handleInvalidEnumException` method in `GlobalExceptionHandler` returns a `400 Bad Request` response with a message indicating the presence of an invalid enum value.

### Other Errors

For other unhandled exceptions, the global exception handler returns a `500 Internal Server Error` response with a general "Internal Server Error" message.

Customize error handling further in the `GlobalExceptionHandler` class to meet the specific needs of your application.

Please note that error responses may include additional details to aid in debugging and troubleshooting.

