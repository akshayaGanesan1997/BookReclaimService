/**
 * BookServiceImpl.java:
 * 
 * Defines a service for managing books and book-related operations in a marketplace application. It includes methods for retrieving, adding, updating, buying, and selling books. It also handles various exceptions related to book operations and maintains the integrity of the marketplace inventory. This class serves as the implementation of the BookService interface.
 *
 * The class structure includes the following sections:
 * - Dependencies: Declarations and initialization of repositories and constants.
 * - Methods for Managing Books: Retrieval, addition, and update of books.
 * - Methods for Buying and Selling Books: Purchase and sale of books, including by ISBN.
 * - Helper Methods: Private methods for creating transaction records.
 *
 * This class is designed to ensure the smooth operation of a book marketplace by providing essential book-related services and maintaining data consistency.
 */

package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.errorhandler.*;
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

    /**
     * Retrieves a list of all books in the marketplace.
     *
     * @return List of all books.
     */
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a list of available books in the marketplace.
     *
     * @return List of available books.
     */
    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks(Book.Status.AVAILABLE);
    }

    /**
     * Retrieves a book by its unique ID.
     *
     * @param bookId The ID of the book to retrieve.
     * @return The book with the given ID.
     * @throws BookNotFoundException if the book does not exist.
     */
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given ID: " + bookId));
    }

    /**
     * Retrieves books by a specified category.
     *
     * @param category The category of books to retrieve.
     * @return List of books in the specified category.
     */
    @Override
    public List<Book> getBooksByCategory(String category) {
        Book.Category bookCategory = Book.Category.valueOf(category);
        return bookRepository.findBooksByCategory(bookCategory);
    }

    /**
     * Retrieves a book by its ISBN (International Standard Book Number).
     *
     * @param isbn The ISBN of the book to retrieve.
     * @return The book with the given ISBN.
     * @throws BookNotFoundException if the book does not exist.
     */
    @Override
    public Book getBookByISBN(String isbn) {
        return Optional.ofNullable(bookRepository.findBookByISBN(isbn))
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given ISBN: " + isbn));
    }

    /**
     * Searches for books based on a keyword.
     *
     * @param keyword The keyword to search for in book titles, descriptions, etc.
     * @return List of books matching the search keyword.
     */
    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findBooksByKeyword(keyword);
    }

    /**
     * Adds a new book to the marketplace.
     *
     * @param newBook The new book to add.
     * @return The added book.
     * @throws InventoryFullException if the inventory is full.
     * @throws BookAlreadyExistsException if a book with the same ISBN already exists.
     */
    @Override
    public Book addBook(Book newBook) {
        if (bookRepository.count() > MAX_INVENTORY_SIZE) {
            throw new InventoryFullException("The inventory is full. No more new books can be added.");
        }
        Book existingBook = bookRepository.findBookByISBN(newBook.getISBN());
        if (existingBook != null) {
            throw new BookAlreadyExistsException("A book with the same ISBN already exists.");
        }

        newBook.setStatus(Book.Status.AVAILABLE);
        return bookRepository.save(newBook);
    }

    /**
     * Updates the information of an existing book.
     *
     * @param book The updated book information.
     * @param id   The ID of the book to update.
     * @return The updated book.
     * @throws BookNotFoundException if the book does not exist.
     */
    @Override
    public Book updateBook(Book book, Long id) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given ID: " + id));

            // Update book attributes
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

        // Update related transactions
        List<Transaction> updatedTransactions = new ArrayList<>();
        for (Transaction transaction : book.getTransactions()) {
            transaction.setBook(existingBook);
            updatedTransactions.add(transaction);
        }
        existingBook.setTransactions(updatedTransactions);
        existingBook.setStatus(book.getStatus());
        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given id: " + id));
        bookRepository.delete(book);
    }

    /**
     * Buys a book for a user.
     *
     * @param userId  The ID of the user buying the book.
     * @param bookId  The ID of the book to buy.
     * @return "Success" if the purchase is successful.
     * @throws UserNotFoundException if the user does not exist.
     * @throws BookNotFoundException if the book does not exist.
     * @throws ValidationException if there are issues with the purchase.
     */
    public String buyBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found for the given id: " + bookId));

        if (book.getStatus() != Book.Status.AVAILABLE) {
            throw new ValidationException(Collections.singletonList("Book is not available for purchase."));
        }

        if (user.getFunds() < book.getCurrentPrice()) {
            throw new ValidationException(Collections.singletonList("Insufficient funds to purchase the book."));
        }

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

        return "Success";
    }

    /**
     * Sells a book for a user.
     *
     * @param userId  The ID of the user selling the book.
     * @param bookId  The ID of the book to sell.
     * @return "Success" if the sale is successful.
     * @throws UserNotFoundException if the user does not exist.
     * @throws BookNotFoundException if the book does not exist.
     * @throws ValidationException if there are issues with the sale.
     */

    public String sellBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found for the given id: " + bookId));

        if (!user.getUserId().equals(userId)) {
            throw new ValidationException(Collections.singletonList("Seller does not own the book with the given ID."));
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

        return "Success";
    }
    
    /**
     * Sells a book by ISBN for a user.
     *
     * @param userId  The ID of the user selling the book.
     * @param isbn    The ISBN of the book to sell.
     * @return "Success" if the sale is successful.
     * @throws UserNotFoundException if the user does not exist.
     * @throws BookNotFoundException if the book does not exist.
     * @throws ValidationException if there are issues with the sale.
     */

    @Override
    public String sellBookByISBN(Long userId, String isbn) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + userId));
        Book book = Optional.ofNullable(bookRepository.findBookByISBN(isbn))
                .orElseThrow(() -> new BookNotFoundException("Book not found for the given ISBN: " + isbn));

        if (!book.getStatus().equals(Book.Status.AVAILABLE)) {
            throw new ValidationException(Collections.singletonList("Book is not available for purchase."));
        }

        double sellingPrice = book.getCurrentPrice();
        user.setFunds(user.getFunds() + sellingPrice);
        book.depreciatePrice();
        book.setStatus(Book.Status.AVAILABLE);

        Transaction transaction = createTransaction(user, book, sellingPrice, Transaction.TransactionType.SELL);
        transactionRepository.save(transaction);

        userRepository.save(user);
        bookRepository.save(book);

        return "Success ";
    }

    /**
     * Creates a transaction record for a book-related action.
     *
     * @param user            The user involved in the transaction.
     * @param book            The book involved in the transaction.
     * @param transactionAmount The amount involved in the transaction.
     * @param transactionType The type of transaction (BUY or SELL).
     * @return The created transaction record.
     */
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
}