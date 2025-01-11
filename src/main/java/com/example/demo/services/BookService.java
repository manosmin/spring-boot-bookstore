package com.example.demo.services;

import com.example.demo.exceptions.BookNotFoundException;
import com.example.demo.models.Book;
import com.example.demo.repositories.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;


    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> {
            logger.info("Book with ID: {} not found.", id);
            return new BookNotFoundException("Book not found.");
        });
    }

    public Page<Book> getBookByAuthor(String author, Pageable pageable) {
        Page<Book> books = bookRepository.findByAuthor(author, pageable);
        if (books.isEmpty()) {
            logger.info("No books found for author: {}", author);
            throw new BookNotFoundException("No books found for the given author.");
        }
        return books;
    }

    public Page<Book> getBookByTitle(String name, Pageable pageable) {
        Page<Book> books = bookRepository.findByTitleContainingIgnoreCase(name, pageable);
        if (books.isEmpty()) {
            logger.info("No books found for title: {}", name);
            throw new BookNotFoundException("No books found with the given title.");
        }
        return books;
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            logger.info("Book with ID: {} not found.", id);
            throw new BookNotFoundException("Book not found.");
        }
    }
}