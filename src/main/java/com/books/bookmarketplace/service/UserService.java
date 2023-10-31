package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User searchUsersByEMailOrUserName(String keyword);
    User addUser(User user);
}
