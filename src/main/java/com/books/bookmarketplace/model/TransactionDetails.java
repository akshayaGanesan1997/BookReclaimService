package com.books.bookmarketplace.model;

import com.books.bookmarketplace.entity.Transaction;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionDetails {
    private Long transactionId;
    private Date transactionDate;
    private Double transactionAmount;
    private Transaction.TransactionStatus status;
    private Transaction.TransactionType transactionType;
    private String transactionNotes;

}
