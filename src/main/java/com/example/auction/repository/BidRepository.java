package com.example.auction.repository;

import com.example.auction.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    
    // Fetches all bids for a specific auction, ordered from newest to oldest
    List<Bid> findByAuctionIdOrderByTimestampDesc(Long auctionId);
}
