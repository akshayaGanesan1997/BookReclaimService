package com.books.bookmarketplace.errorhandler;

import lombok.Getter;

@Getter
public class InvalidEnumException extends RuntimeException {
    private final String error;

    public InvalidEnumException(String error) {
        this.error = error;
    }
}