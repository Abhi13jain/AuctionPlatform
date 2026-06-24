package com.example.auction.service;

import com.example.auction.model.Auction;
import com.example.auction.model.AuctionStatus;
import com.example.auction.model.MotorcycleStatus;
import com.example.auction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * What this does in one plain sentence:
 * This acts as the brain behind the auctions, making sure they open and close automatically at the exact right times.
 *
 * Why the status transition logic works this way:
 * The logic is highly protective: when a SCHEDULED auction becomes ACTIVE, we also update the motorcycle itself to say "IN_AUCTION" so nobody else can buy it. When the auction ends, the status changes to ENDED and the motorcycle becomes "SOLD".
 *
 * What would go wrong if this piece was missing:
 * Auctions would sit in the database forever without ever accepting bids, or they would run infinitely without ever selecting a winner.
 */
@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    /**
     * What this does in one plain sentence:
     * This is a time-robot that wakes up every 60 seconds to check if any auctions need to be started or finished.
     *
     * What @Scheduled does and how Spring runs it automatically:
     * Because we put @EnableScheduling in our main file, Spring creates a background stopwatch. Every time 60,000 milliseconds pass, it automatically runs this exact method without a human having to click a button.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoManageAuctions() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Find auctions that are scheduled and it's time to start them
        List<Auction> scheduledAuctions = auctionRepository.findByStatus(AuctionStatus.SCHEDULED);
        for (Auction auction : scheduledAuctions) {
            if (auction.getStartTime().isBefore(now) || auction.getStartTime().isEqual(now)) {
                startAuction(auction);
            }
        }

        // 2. Find active auctions and see if their time has run out
        List<Auction> activeAuctions = auctionRepository.findByStatus(AuctionStatus.ACTIVE);
        for (Auction auction : activeAuctions) {
            if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                endAuction(auction);
            }
        }
    }

    /**
     * What this does in one plain sentence:
     * Transitions an auction to ACTIVE and tells the motorcycle it's currently locked in an auction.
     */
    public void startAuction(Auction auction) {
        auction.setStatus(AuctionStatus.ACTIVE);
        if (auction.getMotorcycle() != null) {
            auction.getMotorcycle().setStatus(MotorcycleStatus.IN_AUCTION);
        }
        auctionRepository.save(auction);
    }

    public void endAuction(Auction auction) {
        auction.setStatus(AuctionStatus.ENDED);
        if (auction.getMotorcycle() != null) {
            // If there is a winner, the motorcycle is SOLD. If there are no bids, it becomes AVAILABLE again.
            if (auction.getWinnerId() != null) {
                auction.getMotorcycle().setStatus(MotorcycleStatus.SOLD);
            } else {
                auction.getMotorcycle().setStatus(MotorcycleStatus.AVAILABLE);
            }
        }
        auctionRepository.save(auction);
    }

    /**
     * What this does in one plain sentence:
     * Simply fetches a list of all currently running auctions.
     */
    public List<Auction> getActiveAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.ACTIVE);
    }

    /**
     * What this does in one plain sentence:
     * Returns a list of all auctions regardless of status.
     */
    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    /**
     * What this does in one plain sentence:
     * Creates a brand new auction in the database.
     */
    public Auction createAuction(Auction auction) {
        auction.setStatus(AuctionStatus.SCHEDULED);
        return auctionRepository.save(auction);
    }

    /**
     * What this does in one plain sentence:
     * Finds a single auction by its ID so users can view the details page.
     */
    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }
}
