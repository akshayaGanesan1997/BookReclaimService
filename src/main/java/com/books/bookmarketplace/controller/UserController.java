package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.errorhandler.ValidationException;
import com.books.bookmarketplace.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(errors);
        }
        try {
            User newUser = userService.addUser(user);
            logger.log(Level.INFO, "Added new user: " + newUser.getUsername());
            return ResponseEntity.ok().body(newUser);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding a new user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<User> updateUser(@RequestParam(name = "userId") @Positive Long userId, @Valid @RequestBody User user, BindingResult bindingResult) {
        if (userId == null || userId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid user ID. Please provide a positive numeric value."));
        }
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(errors);
        }
        try {
            User updatedUser = userService.updateUser(user, userId);
            logger.log(Level.INFO, "Updated the user: " + updatedUser.getUsername());
            return ResponseEntity.ok().body(updatedUser);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while updating a user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam(name = "userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid user ID. Please provide a positive numeric value."));
        }
        try {
            logger.log(Level.INFO, "Deleting user with ID: " + userId);
            userService.deleteUser(userId);
            logger.log(Level.INFO, "User with ID " + userId + " deleted successfully.");
            return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while deleting the user with ID " + userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the userId: " + e.getMessage());
        }
    }

    @GetMapping("/purchasedBooks")
    public ResponseEntity<List<Book>> purchasedBooks(@RequestParam(name = "userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid user ID. Please provide a positive numeric value."));
        }
        List<Book> purchasedBooks = userService.getPurchasedBooksByUser(userId);

        if (purchasedBooks.isEmpty()) {
            logger.log(Level.INFO, "No books available");
            return ResponseEntity.noContent().build();
        } else {
            logger.log(Level.INFO, "Retrieved " + purchasedBooks.size() + " books purchased by user");
            return ResponseEntity.ok().body(purchasedBooks);
        }
    }

    @GetMapping("/booksSoldByUser")
    public ResponseEntity<List<Book>> booksSoldByUser(@RequestParam(name = "userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid user ID. Please provide a positive numeric value."));
        }
        List<Book> soldBooks = userService.getBooksSoldByUser(userId);

        if (soldBooks.isEmpty()) {
            logger.log(Level.INFO, "No books sold by the user");
            return ResponseEntity.noContent().build();
        } else {
            logger.log(Level.INFO, "Retrieved " + soldBooks.size() + " books sold by user");
            return ResponseEntity.ok().body(soldBooks);
        }
    }

    @PutMapping("/addFunds")
    public ResponseEntity<User> addFundsToUser(
            @RequestParam(name = "userId") @Positive Long userId,
            @RequestParam(name = "funds") @DecimalMin(value = "0.0", message = "Amount must be a non-negative value") Double funds
    ) {
        if (userId == null || userId <= 0) {
            throw new ValidationException(Collections.singletonList("Invalid user ID. Please provide a positive numeric value."));
        }
        if (funds == null || funds <= 0.0) {
            throw new ValidationException(Collections.singletonList("Invalid amount. Please provide a non-negative value."));
        }
        try {
            User updatedUser = userService.addFundsToUser(userId, funds);
            logger.log(Level.INFO, "Added funds to user: " + updatedUser.getUsername());
            return ResponseEntity.ok().body(updatedUser);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding funds to user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
