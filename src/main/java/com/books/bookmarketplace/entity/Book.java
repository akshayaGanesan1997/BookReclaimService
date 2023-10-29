package com.books.bookmarketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.ISBN;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @NotBlank(message = "ISBN is required")
    private String ISBN;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private int edition;

    @NotNull
    @Positive(message = "Original price must be a positive number")
    private Double originalPrice;

    @NotNull
    @Positive(message = "Current price must be a positive number")
    private Double currentPrice;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    private String conditionDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_condition")
    private Condition condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;

    public enum Category {
        FICTION,
        NON_FICTION,
        SCIENCE,
        HISTORY,
        MYSTERY,
        ROMANCE,
        TECHNOLOGY,
        COOKING,
        TRAVEL,
        BIOGRAPHY,
        FANTASY,
        HORROR,
        BUSINESS,
        POETRY,
        PHILOSOPHY,
        RELIGION,
        ART,
        EDUCATION,
        KIDS
    }

    public enum Condition {
        NEW,
        LIKE_NEW,
        GOOD,
        ACCEPTABLE,
        POOR,
        VERY_POOR,
        EXCELLENT,
        FAIR,
        UNUSED
    }

    public enum Status {
        AVAILABLE,
        PENDING,
        SOLD,
        RESERVED,
        OUT_OF_STOCK,
        DISCONTINUED,
        DAMAGED
    }

    public void depreciatePrice() {
        // Calculate the new current price after depreciation
        double depreciationFactor = 0.90; // 10% depreciation
        currentPrice = currentPrice * depreciationFactor;
    }
}
