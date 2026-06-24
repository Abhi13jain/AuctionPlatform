package com.example.auction.dto;

import com.example.auction.model.AuctionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * What this does in one plain sentence:
 * This serves as a secure envelope to send and receive auction details over the internet without exposing our raw database structure.
 *
 * Why the status transition logic works this way:
 * It allows the frontend (like a website) to safely view the current status (SCHEDULED, ACTIVE, ENDED) without being able to force a fake status directly into the database.
 *
 * What would go wrong if this piece was missing:
 * Our database entity would be directly exposed to hackers who might try to manually alter the winner ID or price through web requests.
 */
public class AuctionDTO {
    private Long id;
    private Long motorcycleId;
    private MotorcycleDTO motorcycle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private AuctionStatus status;
    private Long winnerId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMotorcycleId() { return motorcycleId; }
    public void setMotorcycleId(Long motorcycleId) { this.motorcycleId = motorcycleId; }

    public MotorcycleDTO getMotorcycle() { return motorcycle; }
    public void setMotorcycle(MotorcycleDTO motorcycle) { this.motorcycle = motorcycle; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public AuctionStatus getStatus() { return status; }
    public void setStatus(AuctionStatus status) { this.status = status; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }
}
