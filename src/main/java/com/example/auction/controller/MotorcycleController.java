package com.example.auction.controller;

import com.example.auction.dto.MotorcycleDTO;
import com.example.auction.model.Motorcycle;
import com.example.auction.model.MotorcycleStatus;
import com.example.auction.repository.MotorcycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * What it does and why it's needed:
 * This class is the Receptionist for our Motorcycle features. It handles all incoming web requests for URLs starting with "/api/motorcycles".
 * It's needed so the frontend (like a React website) has specific endpoints it can talk to for getting or modifying motorcycles.
 *
 * @RestController:
 * This tells Spring that every method in this class will return data directly (usually in JSON format) rather than returning a visual HTML web page.
 *
 * @RequestMapping("/api/motorcycles"):
 * This sets the base URL. Any method inside this class will automatically be grouped under "yoursite.com/api/motorcycles".
 */
@RestController
@RequestMapping("/api/motorcycles")
public class MotorcycleController {

    @Autowired
    private MotorcycleRepository motorcycleRepository;

    /**
     * What it does and why it's needed:
     * This method fetches all the motorcycles from the database and returns them as a list to anyone who asks.
     * It's needed to display the "Inventory" or "Auction Gallery" on the website.
     *
     * @GetMapping:
     * This maps HTTP GET requests to this method. GET is used when the user just wants to "read" or "fetch" data without changing anything.
     */
    @GetMapping
    public List<MotorcycleDTO> getAllMotorcycles() {
        return motorcycleRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * What it does and why it's needed:
     * This method allows creating a brand new motorcycle listing.
     * It's needed so new items can be added to the auction platform.
     *
     * @PostMapping:
     * Maps HTTP POST requests to this method. POST is used when the user wants to "create" or "submit" new data.
     *
     * @PreAuthorize("hasRole('ADMIN')"):
     * This is the strict security guard! It checks the user's token, and if they don't have the 'ADMIN' role, it instantly blocks the request.
     *
     * @RequestBody:
     * Tells Spring to grab the JSON data from the incoming web request and stuff it into our MotorcycleDTO object.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MotorcycleDTO createMotorcycle(@RequestBody MotorcycleDTO dto) {
        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setTitle(dto.getTitle());
        motorcycle.setDescription(dto.getDescription());
        motorcycle.setBrand(dto.getBrand());
        motorcycle.setYear(dto.getYear());
        motorcycle.setStartingPrice(dto.getStartingPrice());
        motorcycle.setImageUrls(dto.getImageUrls());
        // Default to AVAILABLE if not provided
        motorcycle.setStatus(dto.getStatus() != null ? dto.getStatus() : MotorcycleStatus.AVAILABLE);

        Motorcycle saved = motorcycleRepository.save(motorcycle);
        return convertToDTO(saved);
    }

    /**
     * What it does and why it's needed:
     * Updates an existing motorcycle's details using its ID.
     * It's needed for fixing typos or updating an item's status.
     *
     * @PutMapping("/{id}"):
     * Maps HTTP PUT requests (used for full updates). The "{id}" part means it expects an ID in the URL, like /api/motorcycles/5.
     *
     * @PathVariable Long id:
     * Extracts the "5" from the URL and puts it into the `id` variable.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MotorcycleDTO> updateMotorcycle(@PathVariable Long id, @RequestBody MotorcycleDTO dto) {
        return motorcycleRepository.findById(id).map(existing -> {
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            existing.setBrand(dto.getBrand());
            existing.setYear(dto.getYear());
            existing.setStartingPrice(dto.getStartingPrice());
            existing.setImageUrls(dto.getImageUrls());
            existing.setStatus(dto.getStatus());
            Motorcycle updated = motorcycleRepository.save(existing);
            return ResponseEntity.ok(convertToDTO(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * What it does and why it's needed:
     * Deletes a motorcycle entirely from the database.
     * It's needed to remove listings that were made by mistake or are no longer valid.
     *
     * @DeleteMapping("/{id}"):
     * Maps HTTP DELETE requests to this method.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMotorcycle(@PathVariable Long id) {
        if (!motorcycleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        motorcycleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * What it does and why it's needed:
     * A helper method to transform our raw database `Motorcycle` object into a safer `MotorcycleDTO` envelope.
     * It's needed to prevent exposing the raw entity directly to the web.
     */
    private MotorcycleDTO convertToDTO(Motorcycle motorcycle) {
        MotorcycleDTO dto = new MotorcycleDTO();
        dto.setId(motorcycle.getId());
        dto.setTitle(motorcycle.getTitle());
        dto.setDescription(motorcycle.getDescription());
        dto.setBrand(motorcycle.getBrand());
        dto.setYear(motorcycle.getYear());
        dto.setStartingPrice(motorcycle.getStartingPrice());
        dto.setImageUrls(motorcycle.getImageUrls());
        dto.setStatus(motorcycle.getStatus());
        return dto;
    }
}
