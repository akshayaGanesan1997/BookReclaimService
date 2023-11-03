package com.books.bookmarketplace.errorhandler;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String error;

    public UserAlreadyExistsException(String error) {
        this.error = error;
    }
}