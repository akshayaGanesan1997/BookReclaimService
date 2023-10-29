package com.books.bookmarketplace.errorhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        List<String> errors = ex.getErrors();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Validation errors occurred");
        responseBody.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnumException(HttpMessageNotReadableException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        if (errorMessage.contains("Cannot deserialize value of type")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid enum value provided.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }

}
