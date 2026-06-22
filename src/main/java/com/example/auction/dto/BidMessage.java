package com.example.auction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidMessage {
    private Long auctionId;
    private Long userId;
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
