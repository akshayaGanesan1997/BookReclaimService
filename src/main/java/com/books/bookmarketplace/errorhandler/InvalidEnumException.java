package com.books.bookmarketplace.errorhandler;

public class InvalidEnumException extends RuntimeException {

    public InvalidEnumException(String message) {
        super(message);
    }
}
