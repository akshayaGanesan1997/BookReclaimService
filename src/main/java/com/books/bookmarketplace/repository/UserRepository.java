package com.books.bookmarketplace.repository;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :keyword OR u.email = :keyword")
    Optional<User> findUserByKeyword(@Param("keyword") String keyword);

    @Query("SELECT b FROM User u JOIN u.soldTransactions st JOIN st.book b WHERE u.userId = :userId")
    List<Book> findBooksSoldByUser(Long userId);

    @Query("SELECT b FROM User u JOIN u.purchasedBooks b WHERE u.userId = :userId")
    List<Book> findPurchasedBooksByUser(Long userId);
}
