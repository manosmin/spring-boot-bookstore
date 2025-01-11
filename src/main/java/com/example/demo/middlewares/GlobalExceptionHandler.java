package com.example.demo.middlewares;

import com.example.demo.dtos.ResponseBodyDTO;
import com.example.demo.exceptions.BookNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({BookNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseBodyDTO> handleBookNotFoundException(BookNotFoundException ex) {
        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseBodyDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ResponseBodyDTO.FieldErrorDTO> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    return ResponseBodyDTO.FieldErrorDTO.builder()
                            .field(fieldName)
                            .message(message)
                            .build();
                })
                .collect(Collectors.toList());
        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed.")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ResponseBodyDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        List<ResponseBodyDTO.FieldErrorDTO> errors = ex.getConstraintViolations().stream()
                .map(violation -> ResponseBodyDTO.FieldErrorDTO.builder()
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());
        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed.")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseBodyDTO> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String fieldName = ex.getName();
        ResponseBodyDTO.FieldErrorDTO fieldError = ResponseBodyDTO.FieldErrorDTO.builder()
                .field(fieldName)
                .build();
        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid parameter type")
                .errors(Collections.singletonList(fieldError))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ResponseBodyDTO> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseBodyDTO> handleGenericException() {
        ResponseBodyDTO response = ResponseBodyDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
