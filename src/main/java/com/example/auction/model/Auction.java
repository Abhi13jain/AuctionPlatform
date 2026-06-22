package com.example.auction.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * What this does in one plain sentence:
 * This class acts as the database blueprint representing an ongoing or upcoming auction for a motorcycle.
 *
 * Why the status transition logic works this way:
 * It stores exactly when the auction should start and end, giving our automated system the exact targets it needs to change the status from SCHEDULED to ACTIVE to ENDED.
 * 
 * What would go wrong if this piece was missing:
 * We wouldn't be able to save auctions to the database, meaning nobody could ever bid on motorcycles.
 *
 * @Entity: Tells the database to create a table specifically for this Java class.
 */
@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "motorcycle_id", referencedColumnName = "id")
    private Motorcycle motorcycle;

    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private BigDecimal startingPrice;
    
    private BigDecimal currentPrice;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    private Long winnerId;

    public Auction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Motorcycle getMotorcycle() { return motorcycle; }
    public void setMotorcycle(Motorcycle motorcycle) { this.motorcycle = motorcycle; }

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
