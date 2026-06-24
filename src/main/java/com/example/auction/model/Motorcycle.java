package com.example.auction.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * What it does and why it's needed:
 * This class represents a "Motorcycle" object in our application. It acts as a blueprint for storing motorcycle listings.
 * It's needed so we can save and retrieve motorcycle details from the database.
 *
 * @Entity:
 * This annotation tells Spring Data JPA (our database translator) that this class corresponds to a table in the database. Every object of this class will be a row in that table.
 *
 * @Table(name = "motorcycles"):
 * This tells the database to explicitly name the table "motorcycles" instead of using the default class name.
 *
 * Assumptions made and why:
 * I assumed `startingPrice` should be a `BigDecimal` because it handles money far more accurately than a regular `double` (which can have rounding errors).
 */
@Entity
@Table(name = "motorcycles")
public class Motorcycle {

    /**
     * What it does and why it's needed:
     * This is the unique ID for each motorcycle. It's needed to tell apart two identical motorcycles (like two black 2020 Yamahas).
     *
     * @Id:
     * This marks the field as the "Primary Key" (the unique identifier) in the database table.
     *
     * @GeneratedValue(strategy = GenerationType.IDENTITY):
     * This tells the database to automatically generate a new, unique number for this ID every time we save a new motorcycle.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String brand;
    
    private Integer year;
    
    private BigDecimal startingPrice;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "motorcycle_images", joinColumns = @JoinColumn(name = "motorcycle_id"))
    @Column(name = "image_url", length = 1000)
    private java.util.List<String> imageUrls = new java.util.ArrayList<>();

    /**
     * @Enumerated(EnumType.STRING):
     * This tells the database to save the enum value as a readable text string (like "AVAILABLE") rather than a number (like 0). This makes the database much easier for humans to read.
     */
    @Enumerated(EnumType.STRING)
    private MotorcycleStatus status;

    public Motorcycle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public java.util.List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(java.util.List<String> imageUrls) { this.imageUrls = imageUrls; }

    public MotorcycleStatus getStatus() { return status; }
    public void setStatus(MotorcycleStatus status) { this.status = status; }
}
