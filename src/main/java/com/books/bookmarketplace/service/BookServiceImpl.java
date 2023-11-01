package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.repository.BookRepository;
import com.books.bookmarketplace.repository.TransactionRepository;
import com.books.bookmarketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final int MAX_SIZE = 100;

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
        if (bookRepository.count() > MAX_SIZE) {
            throw new IndexOutOfBoundsException("The inventory is full. No more new books can be added.");
        }
        Book existingBook = bookRepository.findBookByISBN(newBook.getISBN());
        if (existingBook != null) {
            throw new IllegalArgumentException("A book with the same ISBN already exists.");
        }
        newBook.setStatus(Book.Status.AVAILABLE);
        return bookRepository.save(newBook);
    }

    @Override
    public Book updateBook(Book book, Long id) {
        try {
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
                throw new IllegalArgumentException("Book not found for the given ID: " + book.getBookId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteBook(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            bookRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Book not found for the given ID: " + id);
        }
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

            Transaction transaction = new Transaction();
            transaction.setTransactionType(Transaction.TransactionType.BUY);
            transaction.setUser(user);
            transaction.setBook(book);
            LocalDate localDate = LocalDate.now();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            transaction.setTransactionDate(date);
            transaction.setTransactionAmount(buyingPrice);
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
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
        book.depreciatePrice();
        book.setStatus(Book.Status.AVAILABLE);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.SELL);
        transaction.setUser(user);
        transaction.setBook(book);
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        transaction.setTransactionDate(date);
        transaction.setTransactionAmount(sellingPrice);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        userRepository.save(user);
        bookRepository.save(book);

        return "success";
    }

}