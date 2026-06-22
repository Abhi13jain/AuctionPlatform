package com.example.auction.controller;

import com.example.auction.dto.AuctionDTO;
import com.example.auction.model.Auction;
import com.example.auction.model.Motorcycle;
import com.example.auction.repository.MotorcycleRepository;
import com.example.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * What this does in one plain sentence:
 * This provides the web URLs (like a digital menu) that mobile apps or websites can use to create new auctions or view existing ones.
 *
 * Why the status transition logic works this way:
 * By keeping the complex transition logic hidden in the AuctionService, this controller's only job is to receive web requests and hand them safely to the service layer.
 *
 * What would go wrong if this piece was missing:
 * The frontend team wouldn't have any links (API endpoints) to hit to actually display the auctions on the screen.
 */
@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private MotorcycleRepository motorcycleRepository;

    /**
     * What this does in one plain sentence:
     * Allows an Administrator to schedule a new auction for a specific motorcycle.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAuction(@RequestBody AuctionDTO dto) {
        Optional<Motorcycle> motorcycleOpt = motorcycleRepository.findById(dto.getMotorcycleId());
        
        if (motorcycleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Motorcycle not found!");
        }

        Auction auction = new Auction();
        auction.setMotorcycle(motorcycleOpt.get());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setStartingPrice(dto.getStartingPrice());
        auction.setCurrentPrice(dto.getStartingPrice());
        
        Auction savedAuction = auctionService.createAuction(auction);
        return ResponseEntity.ok(convertToDTO(savedAuction));
    }

    /**
     * What this does in one plain sentence:
     * Returns a public list of all the auctions that are currently active right now.
     */
    @GetMapping
    public List<AuctionDTO> getActiveAuctions() {
        return auctionService.getActiveAuctions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * What this does in one plain sentence:
     * Fetches the detailed page of a single specific auction.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuctionDTO> getAuctionById(@PathVariable Long id) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            return ResponseEntity.ok(convertToDTO(auction.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * What this does in one plain sentence:
     * Converts raw database information into a safe DTO envelope.
     */
    private AuctionDTO convertToDTO(Auction auction) {
        AuctionDTO dto = new AuctionDTO();
        dto.setId(auction.getId());
        if (auction.getMotorcycle() != null) {
            dto.setMotorcycleId(auction.getMotorcycle().getId());
        }
        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());
        dto.setStartingPrice(auction.getStartingPrice());
        dto.setCurrentPrice(auction.getCurrentPrice());
        dto.setStatus(auction.getStatus());
        dto.setWinnerId(auction.getWinnerId());
        return dto;
    }
}
