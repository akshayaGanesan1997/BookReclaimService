package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.model.UserDetails;

import java.util.List;

/**
 * This interface defines the operations that can be performed on users within the marketplace.
 */
public interface UserService {
    List<UserDetails> getAllUsers();

    UserDetails getUserById(Long id);

    UserDetails searchUsersByEMailOrUserName(String keyword);

    User addUser(User user);

    User updateUser(User user, Long id);

    void deleteUser(Long id);

    List<Book> getPurchasedBooksByUser(Long id);

    List<Book> getBooksSoldByUser(Long id);

    User addFundsToUser(Long userId, Double amount);
}