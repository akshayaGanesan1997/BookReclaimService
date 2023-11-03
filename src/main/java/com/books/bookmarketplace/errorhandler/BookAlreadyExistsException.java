package com.books.bookmarketplace.errorhandler;

import lombok.Getter;

@Getter
public class BookAlreadyExistsException extends RuntimeException {
    private final String error;

    public BookAlreadyExistsException(String error) {
        this.error = error;
    }
}