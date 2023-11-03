package com.books.bookmarketplace.model;

import lombok.Data;

import java.util.List;

/**
 * A model class representing details of a user in the book marketplace application.
 */
@Data
public class UserDetails {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Double funds;
    private List<TransactionDetails> transactionDetails;
}