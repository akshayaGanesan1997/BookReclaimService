package com.books.bookmarketplace.repository;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t.book FROM Transaction t WHERE t.user.userId = :userId AND t.transactionType = 'SELL'")
    List<Book> findBooksSoldByUser(Long userId);

    @Query("SELECT t.book FROM Transaction t WHERE t.user.userId = :userId AND t.transactionType = 'BUY'")
    List<Book> findPurchasedBooksByUser(Long userId);

}
