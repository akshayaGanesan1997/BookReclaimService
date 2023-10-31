package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.entity.User;
import com.books.bookmarketplace.errorhandler.ValidationException;
import com.books.bookmarketplace.service.UserService;
import jakarta.validation.Valid;
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

}
