package com.example.auction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

/**
 * What @ControllerAdvice does and why centralizing errors is important:
 * Without this, we would have to write `try-catch` blocks in every single controller method. `@ControllerAdvice` acts as a giant safety net underneath the entire application. If any code throws an error, it falls into this net, where we format it beautifully before sending it back to the user.
 *
 * What "production-grade" means for this feature:
 * In production, you never want the user (or a hacker) to see a raw Java Stack Trace (which exposes your internal code). This class intercepts those crashes and translates them into a clean, safe `ErrorResponse`.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles generic, unexpected exceptions (like server crashes).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(), // Note: In strict production, we might hide this message
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles Validation errors (like when a user submits a negative bid amount).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        // Collects all the validation failure messages into one comma-separated string
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
