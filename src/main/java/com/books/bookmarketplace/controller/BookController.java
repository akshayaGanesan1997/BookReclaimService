package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.errorhandler.ValidationException;
import com.books.bookmarketplace.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/getBookById")
    public ResponseEntity<Book> getBookById(@RequestParam(name = "bookId", required = true) @Positive Long bookId) {
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
            return ResponseEntity.status(500).build();
        }
    }

}
