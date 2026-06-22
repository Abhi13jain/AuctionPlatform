package com.example.auction.repository;

import com.example.auction.model.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * What it does and why it's needed:
 * This is the magic interface that talks to our PostgreSQL database. We use it to save, find, and delete motorcycles.
 * It's needed because writing raw SQL code (like "SELECT * FROM motorcycles") in Java is tedious and prone to errors. This interface does all the heavy lifting automatically.
 *
 * @Repository:
 * This annotation tells Spring that this interface is meant to manage database operations. It ensures Spring will automatically generate the implementation for us when the app starts.
 *
 * Assumptions made and why:
 * By extending JpaRepository, I am assuming we just need the standard Create, Read, Update, and Delete (CRUD) methods right now. If we later want to find motorcycles by "brand", we can easily add `findByBrand(String brand)` here.
 */
@Repository
public interface MotorcycleRepository extends JpaRepository<Motorcycle, Long> {
}
