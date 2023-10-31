package com.books.bookmarketplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private Book book;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date must be in the past or present")
    private Date transactionDate;

    @NotNull(message = "Transaction amount is required")
    @Positive(message = "Transaction amount must be a positive number")
    private Double transactionAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Size(max = 255, message = "Transaction notes must not exceed 255 characters")
    private String transactionNotes;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionType transactionType;

    public enum TransactionType {
        BUY,
        SELL
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }

}