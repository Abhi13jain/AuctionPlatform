package com.example.auction.model;

/**
 * What this does in one plain sentence:
 * This defines the three stages of an auction's life.
 *
 * Why the status transition logic works this way:
 * An auction is born as SCHEDULED, wakes up to become ACTIVE when the start time arrives, and finally goes to sleep as ENDED when the time runs out.
 * 
 * What would go wrong if this piece was missing:
 * We wouldn't be able to tell if users are allowed to bid on an item right now.
 */
public enum AuctionStatus {
    SCHEDULED,
    ACTIVE,
    ENDED
}
