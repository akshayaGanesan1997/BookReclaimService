package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    List<Book> getAvailableBooks();
    Book getBookById(Long id);
    List<Book> getBooksByCategory(String category);
    Book getBookByISBN(String isbn);
    List<Book> searchBooks(String keyword);
    Book addBook(Book newBook);
    Book updateBook(Book newBook, Long id);
}
