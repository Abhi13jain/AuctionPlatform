package com.example.auction.controller;

import com.example.auction.dto.BidMessage;
import com.example.auction.model.Bid;
import com.example.auction.repository.BidRepository;
import com.example.auction.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BidController {

    @Autowired
    private BidService bidService;

    @Autowired
    private BidRepository bidRepository;

    /**
     * How a bid message travels from the browser to the server and back:
     * 1. The user clicks "Bid $500" on the website.
     * 2. The browser sends a STOMP message through the open WebSocket tunnel directly to `/app/auction/{auctionId}/bid`.
     * 3. This `@MessageMapping` method catches it. We extract the bid amount and user ID.
     * 4. The `BidService` verifies the bid in milliseconds using Redis.
     * 5. If valid, the `@SendTo` annotation acts like a megaphone and instantly broadcasts the new `$500` price out to the `/topic/auction/{auctionId}` channel.
     * 6. Every other user staring at that auction page instantly sees the price jump to $500 without refreshing their page.
     */
    @MessageMapping("/auction/{auctionId}/bid")
    @SendTo("/topic/auction/{auctionId}")
    public BidMessage processRealTimeBid(@DestinationVariable Long auctionId, BidMessage incomingBid) {
        try {
            // Note: In a fully secure app, we extract the userId from the STOMP header's Principal (the JWT token).
            // Here, we assume the frontend sends their valid userId in the message body.
            return bidService.processBid(auctionId, incomingBid.getUserId(), incomingBid.getAmount());
        } catch (Exception e) {
            // If the bid was too low or auction closed, send back an error message
            BidMessage errorResponse = new BidMessage();
            errorResponse.setAuctionId(auctionId);
            errorResponse.setUserId(incomingBid.getUserId());
            errorResponse.setAmount(incomingBid.getAmount());
            errorResponse.setStatusMessage("ERROR: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * REST endpoint to view the historical log of all bids placed on a specific auction.
     */
    @GetMapping("/api/auctions/{id}/bids")
    public ResponseEntity<List<Bid>> getBidHistory(@PathVariable Long id) {
        List<Bid> bids = bidRepository.findByAuctionIdOrderByTimestampDesc(id);
        return ResponseEntity.ok(bids);
    }
}
