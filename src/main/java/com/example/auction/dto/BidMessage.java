package com.example.auction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * What "production-grade" means for this feature:
 * We don't trust the client. If a hacker intercepts the request and changes the amount to -500, we catch it immediately.
 * Jakarta Bean Validation annotations like @NotNull and @DecimalMin enforce these rules before our code even processes the bid.
 */

public class BidMessage {
    @NotNull(message = "Auction ID cannot be missing")
    private Long auctionId;
    
    @NotNull(message = "User ID cannot be missing")
    private Long userId;
    
    @NotNull(message = "Bid amount must be provided")
    @DecimalMin(value = "0.01", message = "Bid must be greater than zero")
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String statusMessage; // "SUCCESS" or error reason

    public BidMessage() {}

    public Long getAuctionId() { return auctionId; }
    public void setAuctionId(Long auctionId) { this.auctionId = auctionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
