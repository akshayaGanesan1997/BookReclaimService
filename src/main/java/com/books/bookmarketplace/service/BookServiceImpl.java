package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.errorhandler.BookAlreadyExistsException;
import com.books.bookmarketplace.errorhandler.BookNotFoundException;
import com.books.bookmarketplace.errorhandler.InvalidEnumException;
import com.books.bookmarketplace.errorhandler.InventoryFullException;
import com.books.bookmarketplace.repository.BookRepository;
import com.books.bookmarketplace.repository.TransactionRepository;
import com.books.bookmarketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Service for managing books and book-related operations.
 */
@Transactional
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final int MAX_INVENTORY_SIZE = 100;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
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
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given ID: " + bookId));
    }

    @Override
    public List<Book> getBooksByCategory(String category) {
        Book.Category bookCategory = Book.Category.valueOf(category);
        if (!isValidCategory(bookCategory)) {
            throw new InvalidEnumException("Invalid book category: " + bookCategory);
        }
        return bookRepository.findBooksByCategory(bookCategory);
    }

    @Override
    public Book getBookByISBN(String isbn) {
        return Optional.ofNullable(bookRepository.findBookByISBN(isbn))
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given ISBN: " + isbn));
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findBooksByKeyword(keyword);
    }

    @Override
    public Book addBook(Book newBook) {
        if (bookRepository.count() > MAX_INVENTORY_SIZE) {
            throw new InventoryFullException("The inventory is full. No more new books can be added.");
        }
        Book existingBook = bookRepository.findBookByISBN(newBook.getISBN());
        if (existingBook != null) {
            throw new BookAlreadyExistsException("A book with the same isbn already exists.");
        }

        if (!isValidCategory(newBook.getCategory())) {
            throw new InvalidEnumException("Invalid book category: " + newBook.getCategory());
        }

        if (!isValidCondition(newBook.getCondition())) {
            throw new InvalidEnumException("Invalid book condition: " + newBook.getCondition());
        }

        if (!isValidStatus(newBook.getStatus())) {
            throw new InvalidEnumException("Invalid book status: " + newBook.getStatus());
        }

        newBook.setStatus(Book.Status.AVAILABLE);
        return bookRepository.save(newBook);
    }

    @Override
    public Book updateBook(Book book, Long id) {
        if (!isValidCategory(book.getCategory())) {
            throw new InvalidEnumException("Invalid book category: " + book.getCategory());
        }

        if (!isValidCondition(book.getCondition())) {
            throw new InvalidEnumException("Invalid book condition: " + book.getCondition());
        }

        if (!isValidStatus(book.getStatus())) {
            throw new InvalidEnumException("Invalid book status: " + book.getStatus());
        }
        Book existingBook = bookRepository.findById(id).orElse(null);

        if (existingBook != null) {
            existingBook.setISBN(book.getISBN());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setCategory(book.getCategory());
            existingBook.setCondition(book.getCondition());
            existingBook.setDescription(book.getDescription());
            existingBook.setEdition(book.getEdition());
            existingBook.setConditionDescription(book.getConditionDescription());
            existingBook.setCurrentPrice(book.getCurrentPrice());
            existingBook.setOriginalPrice(book.getOriginalPrice());
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
            throw new BookNotFoundException("Book not found for the given ID: " + book.getBookId());
        }
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given id: " + id));
        bookRepository.delete(book);
    }

    public String buyBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user == null || book == null) {
            return "User or book not found.";
        }

        if (book.getStatus() == Book.Status.AVAILABLE && user.getFunds() >= book.getCurrentPrice()) {
            Double buyingPrice = book.getCurrentPrice();
            user.setFunds(user.getFunds() - buyingPrice);
            book.depreciatePrice();
            book.setStatus(Book.Status.SOLD);

            Transaction transaction = createTransaction(user, book, buyingPrice, Transaction.TransactionType.BUY);

            if (book.getCurrentPrice() <= 0) {
                book.setStatus(Book.Status.DISCONTINUED);
            }
            transactionRepository.save(transaction);
            userRepository.save(user);
            bookRepository.save(book);


            return "success";
        } else {
            return "Insufficient funds or book is not available.";
        }
    }

    public String sellBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user == null || book == null) {
            return "User or book not found.";
        }

        if (!user.getUserId().equals(userId)) {
            return "Seller does not own the book with the given ID.";
        }

        double sellingPrice = book.getCurrentPrice();
        user.setFunds(user.getFunds() + sellingPrice);
        double depreciationFactor = 0.90; // 10% depreciation
        double currentPrice = book.getCurrentPrice() * depreciationFactor;
        book.setCurrentPrice(currentPrice);
        book.setStatus(Book.Status.AVAILABLE);

        Transaction transaction = createTransaction(user, book, sellingPrice, Transaction.TransactionType.SELL);
        transactionRepository.save(transaction);

        userRepository.save(user);
        bookRepository.save(book);

        return "success";
    }

    @Override
    public String sellBookByISBN(Long userId, String isbn) {
        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findBookByISBN(isbn);

        if (user == null || book == null) {
            return "User or book not found.";
        }

        if (!book.getStatus().equals(Book.Status.AVAILABLE)) {
            return "Book is not available for selling.";
        }

        double sellingPrice = book.getCurrentPrice();
        user.setFunds(user.getFunds() + sellingPrice);
        book.depreciatePrice();
        book.setStatus(Book.Status.AVAILABLE);

        Transaction transaction = createTransaction(user, book, sellingPrice, Transaction.TransactionType.SELL);
        transactionRepository.save(transaction);

        userRepository.save(user);
        bookRepository.save(book);

        return "success ";
    }

    private Transaction createTransaction(User user, Book book, double transactionAmount, Transaction.TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionType);
        transaction.setUser(user);
        transaction.setBook(book);
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        transaction.setTransactionDate(date);
        transaction.setTransactionAmount(transactionAmount);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        return transaction;
    }

    private boolean isValidCategory(Book.Category category) {
        return Arrays.asList(Book.Category.values()).contains(category);
    }

    private boolean isValidCondition(Book.Condition condition) {
        return Arrays.asList(Book.Condition.values()).contains(condition);
    }

    private boolean isValidStatus(Book.Status status) {
        return Arrays.asList(Book.Status.values()).contains(status);
    }
}