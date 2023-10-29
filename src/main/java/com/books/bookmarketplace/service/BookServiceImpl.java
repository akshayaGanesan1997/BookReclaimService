package com.books.bookmarketplace.service;

import com.books.bookmarketplace.entity.Book;
import com.books.bookmarketplace.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {


    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        return bookOptional.orElse(null);
    }

    @Override
    public Book addBook(Book newBook) {
        return bookRepository.save(newBook);
    }

}
