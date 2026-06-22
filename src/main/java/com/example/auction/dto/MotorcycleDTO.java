package com.example.auction.dto;

import com.example.auction.model.MotorcycleStatus;
import java.math.BigDecimal;

/**
 * What it does and why it's needed:
 * This is a Data Transfer Object (DTO) for the Motorcycle. It's a simplified envelope that carries motorcycle data between the client (like a website) and our server.
 * It's needed because we don't want to expose our raw database entity directly to the web. It also prevents the user from accidentally sending data they shouldn't (like forcing an ID).
 *
 * Assumptions made and why:
 * I assumed we want to send the status back and forth, and that `id` should be included so the client knows which item they are looking at.
 */
public class MotorcycleDTO {

    private Long id;
    private String title;
    private String description;
    private String brand;
    private Integer year;
    private BigDecimal startingPrice;
    private String imageUrl;
    private MotorcycleStatus status;

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public MotorcycleStatus getStatus() { return status; }
    public void setStatus(MotorcycleStatus status) { this.status = status; }
}
