package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.errorhandler.ValidationException;
import com.books.bookmarketplace.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    private static final Logger logger = Logger.getLogger(BookController.class.getName());

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(books);
    }

    @GetMapping("/getAvailableBooks")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        logger.log(Level.INFO, "Fetching all available books");
        List<Book> books = bookService.getAvailableBooks();
        if (books.isEmpty()) {
            logger.log(Level.INFO, "No books available");
            return ResponseEntity.noContent().build();
        } else {
            logger.log(Level.INFO, "Retrieved " + books.size() + " available books");
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/getBookById")
    public ResponseEntity<Book> getBookById(@RequestParam(name = "bookId") @Positive Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid book ID. Please provide a positive numeric value."));
        }
        logger.log(Level.INFO, "Fetching book with ID: " + bookId);
        Book book = bookService.getBookById(bookId);
        if (book != null) {
            logger.log(Level.INFO, "Retrieved book: " + book.getTitle());
            return ResponseEntity.ok().body(book);
        } else {
            logger.log(Level.WARNING, "Book with ID " + bookId + " not found.");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getBooksByCategory")
    public ResponseEntity<List<Book>> getBooksByCategory(@RequestParam(name = "category") String category) {
        List<String> validCategories = Arrays.stream(Book.Category.values())
                .map(Enum::name)
                .toList();

        if (!validCategories.contains(category.toUpperCase())) {
            throw new ValidationException(Collections.singletonList("Invalid category: " + category.toUpperCase()));
        }

        logger.log(Level.INFO, "Fetching all books from category: " + category.toUpperCase());
        List<Book> books = bookService.getBooksByCategory(category.toUpperCase());
        if (books.isEmpty()) {
            logger.log(Level.INFO, "No books found in category: " + category.toUpperCase());
            return ResponseEntity.noContent().build();
        } else {
            logger.log(Level.INFO, "Retrieved " + books.size() + " books in category: " + category.toUpperCase());
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/getBookByISBN")
    public ResponseEntity<Book> getBookByISBN(@RequestParam(name = "isbn") String isbn) {
        try {
            logger.log(Level.INFO, "Fetching book with ISBN: " + isbn);
            Book book = bookService.getBookByISBN(isbn);
            if (book != null) {
                logger.log(Level.INFO, "Retrieved book: " + book.getTitle());
                return ResponseEntity.ok().body(book);
            } else {
                logger.log(Level.WARNING, "Book with ISBN " + isbn + " not found.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching book with ISBN: " + isbn, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/searchBooks")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(name = "keyword") @NotBlank(message = "Search term cannot be blank") String keyword) {
        try {
            logger.log(Level.INFO, "Fetching all books with keyword: " + keyword);
            List<Book> books = bookService.searchBooks(keyword);
            if (books.isEmpty()) {
                logger.log(Level.INFO, "No books found with keyword: " + keyword);
                return ResponseEntity.noContent().build();
            } else {
                logger.log(Level.INFO, "Retrieved " + books.size() + " books with keyword: " + keyword);
                return ResponseEntity.ok(books);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching book with keyword: " + keyword, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/addBook")
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(errors);
        }
        try {
            Book savedBook = bookService.addBook(book);
            logger.log(Level.INFO, "Added new book: " + savedBook.getTitle());
            return ResponseEntity.ok().body(savedBook);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while adding a book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updateBook")
    public ResponseEntity<Book> updateBook(@RequestParam(name = "bookId") @Positive Long bookId, @Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid book ID. Please provide a positive numeric value."));
        }
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(errors);
        }
        try {
            Book updatedBook = bookService.updateBook(book, bookId);
            logger.log(Level.INFO, "Updated the book: " + updatedBook.getTitle());
            return ResponseEntity.ok().body(updatedBook);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while updating a book: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteBook")
    public ResponseEntity<String> deleteBook(@RequestParam(name = "bookId") Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid book ID. Please provide a positive numeric value."));
        }
        try {
            logger.log(Level.INFO, "Deleting book with ID: " + bookId);
            bookService.deleteBook(bookId);
            logger.log(Level.INFO, "Book with ID " + bookId + " deleted successfully.");
            return ResponseEntity.ok("Book with ID " + bookId + " deleted successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while deleting the book with ID " + bookId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the book: " + e.getMessage());
        }
    }

    @PostMapping("/buyBook")
    public ResponseEntity<String> buyBookById(@RequestParam(name = "userId") Long userId, @RequestParam(name = "bookId") Long bookId) {
        try {
            logger.log(Level.INFO, "User with ID " + userId + " is attempting to buy book with ID " + bookId);
            String purchaseResult = bookService.buyBook(userId, bookId);
            if (purchaseResult.equals("success")) {
                logger.log(Level.INFO, "User with ID " + userId + " purchased book with ID " + bookId + " successfully.");
                return ResponseEntity.ok("Book purchased successfully.");
            } else {
                logger.log(Level.WARNING, "User with ID " + userId + " failed to purchase the book: " + purchaseResult);
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/sellBook")
    public ResponseEntity<String> sellBookById(@RequestParam(name = "userId") Long userId, @RequestParam(name = "bookId") Long bookId) {
        try {
            logger.log(Level.INFO, "User with ID " + userId + " is attempting to sell book with ID " + bookId);
            String sellResult = bookService.sellBook(userId, bookId);
            if (sellResult.equals("success")) {
                logger.log(Level.INFO, "User with ID " + userId + " sold book with ID " + bookId + " successfully.");
                return ResponseEntity.ok("Book sold successfully.");
            } else {
                logger.log(Level.WARNING, "User with ID " + userId + " failed to sell the book: " + sellResult);
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}