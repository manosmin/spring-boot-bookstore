package com.example.demo.controllers;

import com.example.demo.dtos.ResponseBodyDTO;
import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Validated
@Tag(name = "Book Controller", description = "Manage books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @Operation(summary = "Get all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found books",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
    })
    @GetMapping
    public ResponseEntity<ResponseBodyDTO> getAllBooks() {
        List<Book> books = bookService.getAllBooks();

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(200)
                .message("Books retrieved successfully.")
                .data(books)
                .build();

        logger.info("Books retrieved successfully. Total books: {}", books.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a specific book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the book",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid id parameter",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
        @ApiResponse(responseCode = "404", description = "Book not found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseBodyDTO> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(200)
                .message("Book retrieved successfully.")
                .data(Collections.singletonList(book))
                .build();

        logger.info("Book with ID: {} retrieved successfully.", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid body",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))})
    })
    @PostMapping
    public ResponseEntity<ResponseBodyDTO> createBook(@Valid @RequestBody Book b) {
        Book book = bookService.createBook(b);

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(201)
                .message("Book created successfully.")
                .data(Collections.singletonList(book))
                .build();

        logger.info("Book with ID: {} created successfully ", book.getId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Delete a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book was deleted",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id parameter",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBodyDTO> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(200)
                .message("Book deleted successfully.")
                .build();

        logger.info("Book with ID: {} deleted successfully.", id);
        return ResponseEntity.ok(response);
    }
}
