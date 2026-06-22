package com.example.auction.model;

/**
 * What it does and why it's needed:
 * This is an Enum (a special data type that only allows specific predefined values) that represents the current state of a motorcycle on our platform.
 * It's needed because we want to strictly control the status of a motorcycle—preventing someone from typing "SOLD_OUT" instead of "SOLD".
 *
 * Assumptions made and why:
 * I assumed these three statuses are enough for a basic listing module. "AVAILABLE" means it's ready to be bidded on, "IN_AUCTION" means an auction is currently running, and "SOLD" means the auction has finished successfully.
 */
public enum MotorcycleStatus {
    AVAILABLE,
    IN_AUCTION,
    SOLD
}
