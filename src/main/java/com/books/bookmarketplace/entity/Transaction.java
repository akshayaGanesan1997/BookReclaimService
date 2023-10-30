package com.books.bookmarketplace.entity;

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

    @NotNull
    @ManyToOne
    @JoinColumn(name = "seller_user_id")
    private User seller;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "buyer_user_id")
    private User buyer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "book_id")
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

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }

}
