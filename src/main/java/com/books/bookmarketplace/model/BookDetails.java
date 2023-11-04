package com.books.bookmarketplace.model;

import lombok.Data;

@Data
public class BookDetails {
    private String ISBN;
    private String title;
    private String author;
    private int edition;
    private int publicationYear;
    private String language;
    private String publisher;
    private Double originalPrice;
    private Double currentPrice;
    private String description;
    private String category;
    private String conditionDescription;
    private int quantity;
    private Long bookId;
    private String condition;
    private String status;
}