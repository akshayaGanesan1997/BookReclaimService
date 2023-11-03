package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.errorhandler.UserAlreadyExistsException;
import com.books.bookmarketplace.errorhandler.UserNotFoundException;
import com.books.bookmarketplace.model.TransactionDetails;
import com.books.bookmarketplace.model.UserDetails;
import com.books.bookmarketplace.repository.TransactionRepository;
import com.books.bookmarketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing users and user-related operations.
 */
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for the given ID: " + userId));
        return convertToUserDetails(user);
    }

    @Override
    public UserDetails searchUsersByEMailOrUserName(String keyword) {
        User user = userRepository.findUserByKeyword(keyword)
                .orElseThrow(() -> new UserNotFoundException("User not found for the given email or username: " + keyword));
        return convertToUserDetails(user);
    }

    @Override
    public User addUser(User user) {
        User existingUser = userRepository.findByEmailOrUsername(user.getEmail(), user.getUsername());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("A user with the same email or username already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, Long id) {

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
            throw new UserNotFoundException("User not found for the given id: " + id);
        }

    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + id));
        userRepository.delete(user);
    }

    @Override
    public List<Book> getPurchasedBooksByUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + id));
        return transactionRepository.findPurchasedBooksByUser(id);
    }

    @Override
    public List<Book> getBooksSoldByUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + id));
        return transactionRepository.findBooksSoldByUser(id);
    }


    @Override
    public User addFundsToUser(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for the given id: " + userId));
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

}