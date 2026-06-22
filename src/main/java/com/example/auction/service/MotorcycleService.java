package com.example.auction.service;

import com.example.auction.model.Motorcycle;
import com.example.auction.repository.MotorcycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * What it does and why it's needed:
 * This class holds the "Business Logic" for our motorcycles. It acts as a middleman between the Controller (which handles web requests) and the Repository (which talks to the database).
 * It's needed because we want to keep complex rules (like checking if an auction is active before deleting a bike) separate from our web routing logic. This keeps the code clean and testable.
 *
 * @Service:
 * This tells Spring that this class is a "Service Component" containing business logic. Spring will automatically create an instance of it and provide it to any controller that needs it.
 *
 * Assumptions made and why:
 * I'm including this service class to follow standard Spring Boot architecture, even though the current controller talks directly to the repository for simplicity. In a real-world app, the controller would call this service instead of the repository.
 */
@Service
public class MotorcycleService {

    /**
     * @Autowired:
     * This tells Spring to automatically plug in a working instance of the MotorcycleRepository here, so we don't have to create it ourselves with the `new` keyword.
     */
    @Autowired
    private MotorcycleRepository motorcycleRepository;

    /**
     * What it does and why it's needed:
     * Fetches all motorcycles from the database.
     * Needed to provide data for the inventory lists.
     */
    public List<Motorcycle> getAllMotorcycles() {
        return motorcycleRepository.findAll();
    }

    /**
     * What it does and why it's needed:
     * Finds a specific motorcycle by its unique ID.
     * Needed when a user clicks on a single motorcycle to view its details.
     */
    public Optional<Motorcycle> getMotorcycleById(Long id) {
        return motorcycleRepository.findById(id);
    }

    /**
     * What it does and why it's needed:
     * Saves a new or updated motorcycle to the database.
     * Needed to persist changes permanently.
     */
    public Motorcycle saveMotorcycle(Motorcycle motorcycle) {
        return motorcycleRepository.save(motorcycle);
    }

    /**
     * What it does and why it's needed:
     * Deletes a motorcycle based on its ID.
     * Needed to remove outdated or invalid listings.
     */
    public void deleteMotorcycle(Long id) {
        motorcycleRepository.deleteById(id);
    }
}
