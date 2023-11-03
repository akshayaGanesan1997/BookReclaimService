
# Book Marketplace Application

![Java](https://img.shields.io/badge/Java-11-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.4-brightgreen)
![Database](https://img.shields.io/badge/MySQL-lightgrey?logo=mysql&style=plastic&logoColor=white&labelColor=blue)

# Table of Contents
1. [Book Marketplace Application](#book-marketplace-application)
   - [Overview](#overview)
   - [Features](#features)
   - [Endpoints](#endpoints)
      - [User Management](#user-management)
      - [Book Management](#book-management)
2. [Technologies Used](#technologies-used)
3. [Project Structure](#project-structure)
4. [Custom Exceptions](#custom-exceptions)
5. [Exception Handling](#exception-handling)
6. [API Documentation](#api-documentation)

## Overview

The Book Marketplace Application is a web-based platform that enables users to buy and sell books. It offers a range of features for managing user accounts, listing books for sale, purchasing books, and handling various book-related operations. This README provides an in-depth overview of the application's capabilities, components, and usage.

## Features

The Book Marketplace Application offers the following key features:

- User Management:
  - Retrieve all users.
  - Retrieve a user by their ID.
  - Search for a user by username or email.
  - Add a new user.
  - Update an existing user.
  - Delete a user by their ID.
  - Retrieve books purchased by a user.
  - Retrieve books sold by a user.
  - Add funds to a user's account.

- Book Management:
  - Retrieve all books or books matching specific criteria.
  - Retrieve a book by its ISBN.
  - Search for books based on a keyword.
  - Add a new book to the marketplace.
  - Update an existing book.
  - Delete a book by its ID.
  - Purchase a book by a user.
  - Sell a book by a user.
  - Sell a book by ISBN.

## Endpoints

### User Management

- **Retrieve All Users**: GET `/users/` - Retrieve a list of all users.
- **Retrieve User by ID**: GET `/users/getUserById?userId={id}` - Retrieve a user by their unique ID.
- **Search Users**: GET `/users/searchUsers?keyword={searchTerm}` - Search for a user by username or email.
- **Add User**: POST `/users/addUser` - Register a new user.
- **Update User**: PUT `/users/updateUser?userId={id}` - Update an existing user.
- **Delete User**: DELETE `/users/deleteUser?userId={id}` - Delete a user by their ID.
- **Retrieve Purchased Books**: GET `/users/purchasedBooks?userId={id}` - Retrieve books purchased by a user.
- **Retrieve Books Sold by User**: GET `/users/booksSoldByUser?userId={id}` - Retrieve books sold by a user.
- **Add Funds to User's Account**: PUT `/users/addFunds?userId={id}&funds={amount}` - Add funds to a user's account.

### Book Management

- **Retrieve All Books**: GET `/books/` - Retrieve a list of all books or books matching specific criteria.
- **Retrieve Book by ISBN**: GET `/books/getBookByISBN?isbn={isbn}` - Retrieve a book by its ISBN.
- **Search Books**: POST `/books/searchBooks?keyword={searchTerm}` - Search for books based on a keyword.
- **Add Book**: POST `/books/addBook` - List a new book in the marketplace.
- **Update Book**: PUT `/books/updateBook?bookId={id}` - Update an existing book.
- **Delete Book**: DELETE `/books/deleteBook?bookId={id}` - Delete a book by its ID.
- **Buy Book**: POST `/books/buyBook` - Purchase a book by a user.
- **Sell Book**: POST `/books/sellBook` - Sell a book by a user.
- **Sell Book by ISBN**: POST `/books/sellBookByISBN` - Sell a book by its ISBN.

## Technologies Used

- **Java**: The primary programming language.
- **Spring Boot**: The framework for building Java applications.
- **Spring Data JPA**: For simplified data access and database interaction.
- **Spring MVC**: For building web applications and RESTful APIs.
- **Lombok**: To reduce boilerplate code.
- **Restful API**
- **MySQL**

## Project Structure

The application is organized as follows:

- `com.books.bookmarketplace.controller`: Contains controller classes for handling HTTP requests and defining API endpoints.
- `com.books.bookmarketplace.entity`: Defines JPA entity classes for users, books, and transactions.
- `com.books.bookmarketplace.model`: Defines model classes for simplified data representation.
- `com.books.bookmarketplace.repository`: Provides repository interfaces for database operations.
- `com.books.bookmarketplace.errorhandler`: Contains custom exception classes and global exception handling.
- `resources`: Configuration files, including application properties and database setup.

## Custom Exceptions

The application includes custom exception classes with specific purposes:

- `UserNotFoundException`: Thrown when a user is not found.
- `UserAlreadyExistsException`: Thrown when a user already exists.
- `BookNotFoundException`: Thrown when a book is not found.
- `BookAlreadyExistsException`: Thrown when a book already exists.
- `InventoryFullException`: Thrown when the inventory is full.
- `ValidationException`: Thrown for validation errors.

## Exception Handling

The `GlobalExceptionHandler` class handles exceptions and provides meaningful error responses. It maps different exceptions to appropriate HTTP status codes and custom error messages.

## API Documentation

The API is documented using Swagger, which provides interactive documentation for all endpoints in swagger.yml file.