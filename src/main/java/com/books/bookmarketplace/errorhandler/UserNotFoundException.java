package com.books.bookmarketplace.errorhandler;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String error;

    public UserNotFoundException(String error) {
        this.error = error;
    }
}