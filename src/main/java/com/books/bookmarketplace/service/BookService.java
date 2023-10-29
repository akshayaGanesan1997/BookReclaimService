package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    Book getBookById(Long id);
    Book addBook(Book newBook);
}
