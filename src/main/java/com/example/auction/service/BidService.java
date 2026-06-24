package com.example.auction.service;

import com.example.auction.dto.BidMessage;
import com.example.auction.model.Auction;
import com.example.auction.model.AuctionStatus;
import com.example.auction.model.Bid;
import com.example.auction.repository.AuctionRepository;
import com.example.auction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private com.example.auction.repository.UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Why Redis is used here instead of hitting the database every time:
     * In an active auction, 50 people might submit a bid in the same single second. If we asked the slow PostgreSQL database to check the current price 50 times a second, it would crash or create massive lag.
     * Redis is an ultra-fast "in-memory" notepad. It stores the `currentPrice` directly in the server's RAM, allowing us to verify if a bid is high enough in milliseconds without ever touching the main database.
     */
    @Transactional
    public BidMessage processBid(Long auctionId, Long userId, BigDecimal amount) throws Exception {
        
        // 1. Validation: Is the user authenticated?
        // What this protects against: Anonymous hackers or logged-out users submitting fake bids.
        if (userId == null) {
            throw new Exception("User is not authenticated");
        }

        // 2. Fetch the auction
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new Exception("Auction not found"));

        // 3. Validation: Is the auction ACTIVE?
        // What this protects against: People trying to bid on an auction before it opens or after it closes.
        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new Exception("Auction is not active");
        }

        // 4. Redis price check
        String redisKey = "auction:" + auctionId + ":price";
        String cachedPriceStr = redisTemplate.opsForValue().get(redisKey);
        BigDecimal currentPrice;

        if (cachedPriceStr != null) {
            currentPrice = new BigDecimal(cachedPriceStr);
        } else {
            // If it's not in Redis yet, read it from the database and put it in Redis
            currentPrice = auction.getCurrentPrice();
            redisTemplate.opsForValue().set(redisKey, currentPrice.toString());
        }

        // 5. Validation: Is the bid higher than the current price?
        // What this protects against: Users trying to submit a $50 bid when the price is already at $100.
        if (amount.compareTo(currentPrice) <= 0) {
            throw new Exception("Bid must be higher than current price");
        }

        // --- All checks passed! Execute the bid. ---

        // Update Redis with the new highest price instantly
        redisTemplate.opsForValue().set(redisKey, amount.toString());

        // Update the database Auction object
        auction.setCurrentPrice(amount);
        auction.setWinnerId(userId);
        auctionRepository.save(auction);

        // Save the bid history record
        Bid bid = new Bid();
        bid.setAuctionId(auctionId);
        bid.setUserId(userId);
        bid.setAmount(amount);
        bid.setTimestamp(LocalDateTime.now());
        bidRepository.save(bid);

        // We need the email for the broadcast message so the frontend can display who bid
        com.example.auction.model.User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));

        // Prepare the success message to broadcast
        BidMessage response = new BidMessage();
        response.setAuctionId(auctionId);
        response.setUserId(userId);
        response.setUserEmail(user.getEmail());
        response.setAmount(amount);
        response.setTimestamp(bid.getTimestamp());
        response.setStatusMessage("SUCCESS");
        return response;
    }
}
