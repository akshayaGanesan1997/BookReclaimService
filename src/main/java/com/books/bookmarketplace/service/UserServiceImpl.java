package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.model.BookDetails;
import com.books.bookmarketplace.model.TransactionDetails;
import com.books.bookmarketplace.model.UserDetails;
import com.books.bookmarketplace.repository.TransactionRepository;
import com.books.bookmarketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<UserDetails> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserDetails)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserDetails(user);
        }
        return null;
    }

    @Override
    public UserDetails searchUsersByEMailOrUserName(String keyword) {
        Optional<User> userOptional = userRepository.findUserByKeyword(keyword);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return convertToUserDetails(user);
        }
        return null;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, Long id) {
        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser != null) {
                existingUser.setEmail(user.getEmail());
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setPassword(user.getPassword());
                existingUser.setPhoneNumber(user.getPhoneNumber());
                existingUser.setFunds(user.getFunds());
                existingUser.setUsername(user.getUsername());
                List<Transaction> updatedTransactions = new ArrayList<>();
                for (Transaction transaction : user.getTransactions()) {
                    transaction.setUser(existingUser);
                    updatedTransactions.add(transaction);
                }
                existingUser.setTransactions(updatedTransactions);
                return userRepository.save(existingUser);
            } else {
                throw new IllegalArgumentException("User not found for the given ID: " + user.getUserId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("User not found for the given ID:" + id);
        }
    }

    @Override
    public List<Book> getPurchasedBooksByUser(Long id) {
        return transactionRepository.findPurchasedBooksByUser(id);
    }

    @Override
    public List<Book> getBooksSoldByUser(Long id) {
        return transactionRepository.findBooksSoldByUser(id);
    }

    @Override
    public User addFundsToUser(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
        user.setFunds(user.getFunds() + amount);
        return userRepository.save(user);
    }

    private UserDetails convertToUserDetails(User user) {
        UserDetails userDetails = new UserDetails();
        userDetails.setUserId(user.getUserId());
        userDetails.setUsername(user.getUsername());
        userDetails.setEmail(user.getEmail());
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());
        userDetails.setPhoneNumber(user.getPhoneNumber());
        userDetails.setFunds(user.getFunds());

        // Convert the lists of transactions and books to their respective DTOs
        List<TransactionDetails> transactionDetails = user.getTransactions().stream()
                .map(this::convertToTransactionDetails)
                .collect(Collectors.toList());
        userDetails.setTransactionDetails(transactionDetails);

        return userDetails;
    }

    private TransactionDetails convertToTransactionDetails(Transaction transaction) {
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTransactionId(transaction.getTransactionId());
        transactionDetails.setTransactionDate(transaction.getTransactionDate());
        transactionDetails.setTransactionAmount(transaction.getTransactionAmount());
        transactionDetails.setStatus(transaction.getStatus());
        transactionDetails.setTransactionNotes(transaction.getTransactionNotes());
        transactionDetails.setTransactionType(transaction.getTransactionType());

        return transactionDetails;
    }

    private BookDetails convertToBookDetails(Book book) {
        BookDetails bookDetails = new BookDetails();
        bookDetails.setBookId(book.getBookId());
        bookDetails.setIsbn(book.getISBN());
        bookDetails.setTitle(book.getTitle());
        bookDetails.setAuthor(book.getAuthor());
        bookDetails.setEdition(book.getEdition());
        bookDetails.setPublicationYear(book.getPublicationYear());
        bookDetails.setLanguage(book.getLanguage());
        bookDetails.setPublisher(book.getPublisher());
        bookDetails.setOriginalPrice(book.getOriginalPrice());
        bookDetails.setCurrentPrice(book.getCurrentPrice());
        bookDetails.setDescription(book.getDescription());
        bookDetails.setCategory(book.getCategory());
        bookDetails.setConditionDescription(book.getConditionDescription());
        bookDetails.setCondition(book.getCondition());
        bookDetails.setStatus(book.getStatus());

        return bookDetails;
    }

}
