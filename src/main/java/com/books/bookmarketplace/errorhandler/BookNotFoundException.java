package com.books.bookmarketplace.errorhandler;

import lombok.Getter;

@Getter
public class BookNotFoundException extends RuntimeException {
    private final String error;

    public BookNotFoundException(String error) {
        this.error = error;
    }
}