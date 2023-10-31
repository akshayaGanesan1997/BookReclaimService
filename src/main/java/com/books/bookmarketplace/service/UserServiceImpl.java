package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.repository.TransactionRepository;
import com.books.bookmarketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }

    @Override
    public User searchUsersByEMailOrUserName(String keyword) {
        Optional<User> userOptional = userRepository.findUserByKeyword(keyword);
        return userOptional.orElse(null);
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

}
