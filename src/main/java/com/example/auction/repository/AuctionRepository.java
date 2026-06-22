package com.example.auction.repository;

import com.example.auction.model.Auction;
import com.example.auction.model.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * What this does in one plain sentence:
 * This interface automatically talks to PostgreSQL so we can easily search for or save auctions.
 *
 * Why the status transition logic works this way:
 * We need custom methods like `findByStatus` so our automated time-bot can quickly grab a list of all SCHEDULED auctions and check if they are ready to become ACTIVE.
 *
 * What would go wrong if this piece was missing:
 * We would have to manually write thousands of lines of SQL to search our database for active auctions.
 */
@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    
    /**
     * What this does in one plain sentence:
     * Asks the database for a list of all auctions that currently have a specific status (like all ACTIVE auctions).
     */
    List<Auction> findByStatus(AuctionStatus status);
}
