/**
 * TransactionDetails.java
 * 
 * Defines the "TransactionDetails" model class for the book marketplace application.
 * It represents a simplified view of a transaction's details and is used for data transfer and presentation.
 *
 * Key Features:
 * - Utilizes Lombok to generate getters, setters, equals, hashCode, and toString methods for convenience.
 * - Contains fields to represent essential information about a transaction, including its unique identifier,
 *   date, amount, status, type, and optional notes.
 *
 * The class is designed to streamline the handling and presentation of transaction information within the application,
 * offering a compact and structured representation of transaction details.
 */

package com.books.bookmarketplace.model;

import com.books.bookmarketplace.entity.Transaction;
import lombok.Data;

import java.util.Date;

/**
 * A model class representing details of a transaction in the book marketplace application.
 */
@Data // Lombok annotation that generates getters, setters, equals, hashCode, and toString methods.
public class TransactionDetails {
    private Long transactionId; // Unique identifier for the transaction.
    private Date transactionDate; // Date when the transaction occurred.
    private Double transactionAmount; // The amount of money involved in the transaction.
    private Transaction.TransactionStatus status; // Represents the status of the transaction.
    private Transaction.TransactionType transactionType; // Represents the type of transaction (BUY or SELL).
    private String transactionNotes; // Optional notes or comments related to the transaction.
}