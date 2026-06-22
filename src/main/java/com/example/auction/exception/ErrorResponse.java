package com.example.auction.exception;

import java.time.LocalDateTime;

/**
 * What "production-grade" means for this feature:
 * Instead of letting the server spit out messy, unpredictable error text when something breaks, this class forces every single error to look exactly the same (a structured JSON object). This makes it easy for the frontend to read and display errors to the user.
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}
