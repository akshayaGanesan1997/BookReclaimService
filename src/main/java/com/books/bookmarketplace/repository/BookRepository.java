package com.books.bookmarketplace.repository;

import com.books.bookmarketplace.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.category = :category")
    List<Book> findBooksByCategory(@Param("category") Book.Category category);

    @Query("SELECT b FROM Book b WHERE b.ISBN = :ISBN")
    Book findBookByISBN(@Param("ISBN") String ISBN);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> findBooksByKeyword(@Param("keyword") String keyword);

    @Query("SELECT b FROM Book b WHERE b.status = :status")
    List<Book> findAvailableBooks(@Param("status") Book.Status status);
}
