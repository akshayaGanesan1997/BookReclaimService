package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.errorhandler.ValidationException;
import com.books.bookmarketplace.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private static final Logger logger = Logger.getLogger(BookController.class.getName());

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/getUserById")
    public ResponseEntity<User> getUserById(@RequestParam(name = "userId") @Positive Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid user ID. Please provide a positive numeric value."));
        }
        logger.log(Level.INFO, "Fetching user with ID: " + userId);
        User user = userService.getUserById(userId);
        if (user != null) {
            logger.log(Level.INFO, "Retrieved user: " + user.getUsername());
            return ResponseEntity.ok().body(user);
        } else {
            logger.log(Level.WARNING, "User with ID " + userId + " not found.");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<User> searchUsers(@RequestParam(name = "keyword") @NotBlank(message = "Search term cannot be blank") String keyword) {
        try {
            logger.log(Level.INFO, "Fetching user with username/email: " + keyword);
            User user = userService.searchUsersByEMailOrUserName(keyword);
            if (user != null) {
                logger.log(Level.INFO, "Retrieved " + user.getFirstName() + " " + user.getLastName());
                return ResponseEntity.ok(user);
            } else {
                logger.log(Level.WARNING, "User with username/email " + keyword + " not found.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching user with username/email: " + keyword, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
