package com.books.bookmarketplace.model;

import com.books.bookmarketplace.entity.Book;
import lombok.Data;

@Data
public class BookDetails {

    private Long bookId;
    private String title;
    private String author;
    private int edition;
    private int publicationYear;
    private String language;
    private String publisher;
    private Double originalPrice;
    private Double currentPrice;
    private String description;
    private Book.Category category;
    private String conditionDescription;
    private Book.Condition condition;
    private Book.Status status;
    private String isbn;
}
