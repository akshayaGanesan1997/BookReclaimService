package com.books.bookmarketplace.errorhandler;

import lombok.Getter;

@Getter
public class InventoryFullException extends RuntimeException {
    private final String error;

    public InventoryFullException(String error) {
        this.error = error;
    }
}