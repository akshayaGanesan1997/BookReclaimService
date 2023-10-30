package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks(Book.Status.AVAILABLE);
    }

    public Book getBookById(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        return bookOptional.orElse(null);
    }

    @Override
    public List<Book> getBooksByCategory(String category) {
        Book.Category bookCategory = Book.Category.valueOf(category);
        return bookRepository.findBooksByCategory(bookCategory);
    }

    @Override
    public Book getBookByISBN(String isbn) {
        return bookRepository.findBookByISBN(isbn);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findBooksByKeyword(keyword);
    }


    @Override
    public Book addBook(Book newBook) {
        return bookRepository.save(newBook);
    }

    @Override
    public Book updateBook(Book book, Long id) {
        try {
            Book existingBook = bookRepository.findById(id).orElse(null);
            if (existingBook != null) {
                existingBook.setISBN(book.getISBN());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setBuyer_user(book.getBuyer_user());
                existingBook.setCategory(book.getCategory());
                existingBook.setCondition(book.getCondition());
                existingBook.setDescription(book.getDescription());
                existingBook.setEdition(book.getEdition());
                existingBook.setConditionDescription(book.getConditionDescription());
                existingBook.setCurrentPrice(book.getCurrentPrice());
                existingBook.setOriginalPrice(book.getOriginalPrice());
                existingBook.setSeller_user(book.getSeller_user());
                existingBook.setTitle(book.getTitle());
                List<Transaction> updatedTransactions = new ArrayList<>();
                for (Transaction transaction : book.getTransactions()) {
                    transaction.setBook(existingBook);
                    updatedTransactions.add(transaction);
                }
                existingBook.setTransactions(updatedTransactions);
                existingBook.setStatus(book.getStatus());
                return bookRepository.save(existingBook);
            } else {
                throw new IllegalArgumentException("Book not found for the given ID: " + book.getBookId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
