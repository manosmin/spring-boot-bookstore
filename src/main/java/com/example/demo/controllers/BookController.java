package com.example.demo.controllers;

import com.example.demo.dtos.ResponseBodyDTO;
import com.example.demo.exceptions.PageNotFoundException;
import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

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
    public ResponseEntity<ResponseBodyDTO> getAllBooks(
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "10") @Min(5) int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> books = bookService.getAllBooks(pageable);

        if (page > books.getTotalPages()) {
            throw new PageNotFoundException("Page does not exist.");
        }

        ResponseBodyDTO.MetadataDTO paginationInfo = ResponseBodyDTO.MetadataDTO.builder()
                .page(books.getNumber() + 1)
                .size(size)
                .totalPages(books.getTotalPages())
                .totalItems(books.getTotalElements())
                .build();

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(200)
                .message("Books retrieved successfully.")
                .data(books.getContent())
                .metadata(paginationInfo)
                .build();

        logger.info("Books retrieved successfully. Total books: {}", books.getContent().size());
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

    @Operation(summary = "Get books by author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found books for author",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
            @ApiResponse(responseCode = "404", description = "No books found for author",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))})
    })
    @GetMapping("/author/{name}")
    public ResponseEntity<ResponseBodyDTO> getBookByAuthor(@PathVariable String name,
       @RequestParam(defaultValue = "1") @Min(1) int page,
       @RequestParam(defaultValue = "10") @Min(5) int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> books = bookService.getBookByAuthor(name, pageable);

        if (page > books.getTotalPages()) {
            throw new PageNotFoundException("Page does not exist.");
        }

        ResponseBodyDTO.MetadataDTO paginationInfo = ResponseBodyDTO.MetadataDTO.builder()
                .page(books.getNumber() + 1)
                .size(size)
                .totalPages(books.getTotalPages())
                .totalItems(books.getTotalElements())
                .build();

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(200)
                .message("Books retrieved successfully.")
                .data(books.getContent())
                .metadata(paginationInfo)
                .build();

        logger.info("Books for {} retrieved successfully.", name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get books by title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found books with title",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid title parameter",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))}),
            @ApiResponse(responseCode = "404", description = "No books found for the given title",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBodyDTO.class))})
    })
    @GetMapping("/title/{name}")
    public ResponseEntity<ResponseBodyDTO> getBookByTitle(
        @PathVariable @Valid @Size(min = 3, message = "Title must be at least 3 characters long.") String name,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "10") @Min(5) int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> books = bookService.getBookByTitle(name, pageable);

        if (page > books.getTotalPages()) {
            throw new PageNotFoundException("Page does not exist.");
        }

        ResponseBodyDTO.MetadataDTO paginationInfo = ResponseBodyDTO.MetadataDTO.builder()
                .page(books.getNumber() + 1)
                .size(size)
                .totalPages(books.getTotalPages())
                .totalItems(books.getTotalElements())
                .build();

        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(200)
                .message("Books retrieved successfully.")
                .data(books.getContent())
                .metadata(paginationInfo)
                .build();

        logger.info("Books with title {} retrieved successfully.", name);
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
