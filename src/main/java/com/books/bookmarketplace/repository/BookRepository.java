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
}
