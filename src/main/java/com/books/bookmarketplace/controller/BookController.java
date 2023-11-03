/**
 * BookController.java
 * 
 * This class acts as the controller for managing book-related operations within the Book Marketplace application.
 * It handles incoming HTTP requests related to books, including retrieving books by various criteria,
 * adding, updating, and deleting books, as well as buying and selling books. The controller interfaces with
 * the BookService to execute these operations.
 * 
 * Endpoints:
 * - GET /books/                 : Retrieve all books or books matching specific criteria.
 * - GET /books/getBookByISBN?isbn={isbn} : Retrieve a book by its ISBN.
 * - POST /books/searchBooks?keyword={searchTerm} : Search for books based on a keyword.
 * - POST /books/addBook          : Add a new book to the marketplace.
 * - PUT /books/updateBook?bookId={id}    : Update an existing book.
 * - DELETE /books/deleteBook?bookId={id} : Delete a book by its ID.
 * - POST /books/buyBook          : Purchase a book by a user.
 * - POST /books/sellBook         : Sell a book by a user.
 * - POST /books/sellBookByISBN   : Sell a book by ISBN.
 * 
 * This class integrates validation using annotations to ensure the integrity of incoming data. It also
 * includes comprehensive error handling for various exceptional situations and logs events and errors using a Logger.
 */

package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.errorhandler.BookAlreadyExistsException;
import com.books.bookmarketplace.errorhandler.BookNotFoundException;
import com.books.bookmarketplace.errorhandler.UserNotFoundException;
import com.books.bookmarketplace.errorhandler.ValidationException;
import com.books.bookmarketplace.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for managing book-related operations.
 * This class handles requests related to books,
 * including retrieving, adding, updating, selling, buying, and deleting books.
 */
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    private static final Logger logger = Logger.getLogger(BookController.class.getName());

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET Request to retrieve all books
    @GetMapping("/")
    public ResponseEntity < List < Book >> getAllBooks() {
        try {
            // Retrieve all books from the service
            List < Book > books = bookService.getAllBooks();
            if (books.isEmpty()) {
                // If no books are found, return a no-content response
                return ResponseEntity.noContent().build();
            }
            // If books are found, return them in the response body
            return ResponseEntity.ok().body(books);
        } catch (Exception e) {
            // Handle any exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET Request to retrieve available books
    @GetMapping("/getAvailableBooks")
    public ResponseEntity < List < Book >> getAvailableBooks() {
        try {
            logger.log(Level.INFO, "Fetching all available books");
            // Retrieve available books from the service
            List < Book > books = bookService.getAvailableBooks();
            if (books.isEmpty()) {
                logger.log(Level.INFO, "No books available");
                // If no available books are found, return a no-content response
                return ResponseEntity.noContent().build();
            } else {
                logger.log(Level.INFO, "Retrieved " + books.size() + " available books");
                // If available books are found, return them in the response body
                return ResponseEntity.ok().body(books);
            }
        } catch (Exception e) {
            // Handle any exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET Request to retrieve a book by its ID
    @GetMapping("/getBookById")
    public ResponseEntity < Book > getBookById(@Valid @RequestParam(name = "bookId") @Positive Long bookId) {
        if (bookId == null || bookId <= 0) {
            // Validate the input, and if it's invalid, throw a ValidationException
            throw new ValidationException(Collections.singletonList("Invalid book ID. Please provide a positive numeric value."));
        }
        try {
            logger.log(Level.INFO, "Fetching book with ID: " + bookId);
            // Retrieve a book by its ID from the service
            Book book = bookService.getBookById(bookId);
            logger.log(Level.INFO, "Retrieved book: " + book.getTitle());
            // Return the retrieved book in the response body
            return ResponseEntity.ok().body(book);
        } catch (BookNotFoundException | ValidationException e) {
            // Handle specific exceptions by rethrowing them
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET Request to retrieve books by category
    @GetMapping("/getBooksByCategory")
    public ResponseEntity < List < Book >> getBooksByCategory(@RequestParam(name = "category") String category) {
        if (category.isBlank()) {
            // Validate that the category is provided, and if it's empty, throw a ValidationException
            throw new ValidationException(Collections.singletonList("Category is required"));
        }
        List < String > validCategories = Arrays.stream(Book.Category.values())
                .map(Enum::name)
                .toList();
        if (!validCategories.contains(category.toUpperCase())) {
            // Validate that the provided category is valid, and if not, throw a ValidationException
            throw new ValidationException(Collections.singletonList("Invalid category: " + category.toUpperCase()));
        }
        try {
            logger.log(Level.INFO, "Fetching all books from category: " + category.toUpperCase());
            // Retrieve books from the specified category
            List < Book > books = bookService.getBooksByCategory(category.toUpperCase());
            if (books.isEmpty()) {
                logger.log(Level.INFO, "No books found in category: " + category.toUpperCase());
                // If no books are found, return a no-content response
                return ResponseEntity.noContent().build();
            } else {
                logger.log(Level.INFO, "Retrieved " + books.size() + " books in category: " + category.toUpperCase());
                // If books are found, return them in the response body
                return ResponseEntity.ok(books);
            }
        } catch (ValidationException e) {
            // Handle ValidationException by rethrowing it
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET Request to retrieve a book by ISBN
    @GetMapping("/getBookByISBN")
    public ResponseEntity < Book > getBookByISBN(@RequestParam(name = "isbn") String isbn) {
        if (isbn.isBlank()) {
            // Validate that ISBN is provided, and if it's empty, throw a ValidationException
            throw new ValidationException(Collections.singletonList("ISBN is required"));
        }
        try {
            logger.log(Level.INFO, "Fetching book with ISBN: " + isbn);
            // Retrieve a book by its ISBN
            Book book = bookService.getBookByISBN(isbn);
            logger.log(Level.INFO, "Retrieved book: " + book.getTitle());
            // Return the retrieved book in the response body
            return ResponseEntity.ok().body(book);
        } catch (BookNotFoundException | ValidationException e) {
            // Handle specific exceptions by rethrowing them
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // POST Request to search for books by a keyword
    @GetMapping("/searchBooks")
    public ResponseEntity < List < Book >> searchBooks(@RequestParam(name = "keyword") String keyword) {
        if (keyword.isBlank()) {
            // Validate that the search keyword is provided, and if it's empty, throw a ValidationException
            throw new ValidationException(Collections.singletonList("Search term cannot be blank."));
        }
        try {
            logger.log(Level.INFO, "Fetching all books with keyword: " + keyword);
            // Search for books using the provided keyword
            List < Book > books = bookService.searchBooks(keyword);
            if (books.isEmpty()) {
                logger.log(Level.INFO, "No books found with keyword: " + keyword);
                // If no books are found, return a no-content response
                return ResponseEntity.noContent().build();
            } else {
                logger.log(Level.INFO, "Retrieved " + books.size() + " books with keyword: " + keyword);
                // If books are found, return them in the response body
                return ResponseEntity.ok(books);
            }
        } catch (ValidationException e) {
            // Handle ValidationException by rethrowing it
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST Request to add a new book
    @PostMapping("/addBook")
    public ResponseEntity < Book > addBook(@Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Validate the input, and if there are binding errors, throw a ValidationException
            List < String > errors = new ArrayList < > ();
            for (FieldError error: bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(errors);
        }
        try {
            // Add a new book and return the saved book in the response
            Book savedBook = bookService.addBook(book);
            logger.log(Level.INFO, "Added new book: " + savedBook.getTitle());
            return ResponseEntity.ok().body(savedBook);
        } catch (BookAlreadyExistsException | HttpMessageNotReadableException e) {
            // Handle specific exceptions by rethrowing them
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            logger.log(Level.SEVERE, "Error adding a new book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT Request to update a book
    @PutMapping("/updateBook")
    public ResponseEntity < Book > updateBook(@RequestParam(name = "bookId") @Positive Long bookId, @Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bookId == null || bookId <= 0) {
            // Validate the book ID, and if it's invalid, throw a ValidationException
            throw new ValidationException(Collections.singletonList("Invalid book ID. Please provide a positive numeric value."));
        }
        if (bindingResult.hasErrors()) {
            // Validate the input, and if there are binding errors, throw a ValidationException
            List < String > errors = new ArrayList < > ();
            for (FieldError error: bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(errors);
        }
        try {
            // Update the book and return the updated book in the response
            Book updatedBook = bookService.updateBook(book, bookId);
            logger.log(Level.INFO, "Updated the book: " + updatedBook.getTitle());
            return ResponseEntity.ok().body(updatedBook);
        } catch (BookNotFoundException | HttpMessageNotReadableException | ValidationException e) {
            // Handle specific exceptions by rethrowing them
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE Request to delete a book by ID
    @DeleteMapping("/deleteBook")
    public ResponseEntity < String > deleteBook(@RequestParam(name = "bookId") Long bookId) {
        if (bookId == null || bookId <= 0) {
            // Validate the book ID, and if it's invalid, throw a ValidationException
            throw new ValidationException(Collections.singletonList("Invalid book ID. Please provide a positive numeric value."));
        }
        try {
            logger.log(Level.INFO, "Deleting book with ID: " + bookId);
            // Delete the book by ID
            bookService.deleteBook(bookId);
            logger.log(Level.INFO, "Book with ID " + bookId + " deleted successfully.");
            return ResponseEntity.ok("Book with ID " + bookId + " deleted successfully.");
        } catch (BookNotFoundException | ValidationException e) {
            // Handle specific exceptions by rethrowing them
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST Request to allow a user to buy a book by book ID
    @PostMapping("/buyBook")
    public ResponseEntity < String > buyBookById(@RequestParam(name = "userId") Long userId, @RequestParam(name = "bookId") Long bookId) {
        try {
            // Log that a user is attempting to buy a book with the given user and book IDs
            logger.log(Level.INFO, "User with ID " + userId + " is attempting to buy book with ID " + bookId);
            // Call the service to facilitate the purchase of the book
            bookService.buyBook(userId, bookId);
            // Log a successful purchase event
            logger.log(Level.INFO, "User with ID " + userId + " purchased book with ID " + bookId + " successfully.");
            // Return a success message in the response
            return ResponseEntity.ok("Book purchased successfully.");
        } catch (BookNotFoundException | UserNotFoundException | ValidationException e) {
            // Handle specific exceptions by returning a bad request response with an error message
            logger.log(Level.WARNING, "User with ID " + userId + " failed to purchase the book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            logger.log(Level.SEVERE, "An error occurred while processing the request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    // POST Request to allow a user to sell a book by book ID
    @PostMapping("/sellBook")
    public ResponseEntity < String > sellBookById(@RequestParam(name = "userId") Long userId, @RequestParam(name = "bookId") Long bookId) {
        try {
            // Log that a user is attempting to sell a book with the given user and book IDs
            logger.log(Level.INFO, "User with ID " + userId + " is attempting to sell book with ID " + bookId);
            // Call the service to facilitate the sale of the book
            bookService.sellBook(userId, bookId);
            // Log a successful sale event
            logger.log(Level.INFO, "User with ID " + userId + " sold book with ID " + bookId + " successfully.");
            // Return a success message in the response
            return ResponseEntity.ok("Book sold successfully.");
        } catch (BookNotFoundException | UserNotFoundException | ValidationException e) {
            // Handle specific exceptions by returning a bad request response with an error message
            logger.log(Level.WARNING, "User with ID " + userId + " failed to sell the book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            logger.log(Level.SEVERE, "An error occurred while processing the request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    // POST Request to allow a user to sell a book by ISBN
    @PostMapping("/sellBookByISBN")
    public ResponseEntity < String > sellBookByISBN(@RequestParam(name = "userId") Long userId, @RequestParam(name = "isbn") String isbn) {
        try {
            // Log that a user is attempting to sell a book with the given user and ISBN
            logger.log(Level.INFO, "User with ID " + userId + " is attempting to sell book with ISBN " + isbn);
            // Call the service to facilitate the sale of the book by ISBN
            bookService.sellBookByISBN(userId, isbn);
            // Log a successful sale event
            logger.log(Level.INFO, "User with ID " + userId + " sold book with ISBN " + isbn + " successfully.");
            // Return a success message in the response
            return ResponseEntity.ok("Book sold successfully.");
        } catch (BookNotFoundException | UserNotFoundException | ValidationException e) {
            // Handle specific exceptions by returning a bad request response with an error message
            logger.log(Level.WARNING, "User with ID " + userId + " failed to sell the book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions by returning an internal server error
            logger.log(Level.SEVERE, "An error occurred while processing the request.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

}