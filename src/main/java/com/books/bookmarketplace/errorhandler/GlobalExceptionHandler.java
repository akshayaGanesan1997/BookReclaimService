package com.books.bookmarketplace.controller;

import com.books.bookmarketplace.errorhandler.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        String error = ex.getError();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "User not found");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        String error = ex.getError();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "User already exists");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Object> handleBookNotFoundException(BookNotFoundException ex) {
        String error = ex.getError();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Book not found");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<Object> handleBookAlreadyExistsException(BookAlreadyExistsException ex) {
        String error = ex.getError();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Book already exists");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
    }

    @ExceptionHandler(InventoryFullException.class)
    public ResponseEntity<Object> handleInventoryFullException(InventoryFullException ex) {
        String error = ex.getError();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Inventory Full");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        List<String> errors = ex.getErrors();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Validation errors occurred");
        responseBody.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String error = "Invalid enum category. Provide valid enum value which are defined";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Enum validation");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleBadRequestException(MethodArgumentTypeMismatchException ex) {
        String error = ex.getMessage();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Bad Request");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParametersException(MissingServletRequestParameterException ex) {
        String error = ex.getMessage();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Bad Request");
        responseBody.put("errors", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

}