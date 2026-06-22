# Spring Boot Auction Module & Automation

This document explains the **Auction Module** built for the platform. This module introduces a fully automated, time-based lifecycle to manage when auctions open and close without requiring human intervention.

---

## 🏗️ 1. The Core Architecture

We created the foundational classes to represent an auction in our database:

* **`AuctionStatus.java`**: A strict Enum with exactly three phases: `SCHEDULED` (waiting to start), `ACTIVE` (currently accepting bids), and `ENDED` (finished).
* **`Auction.java`**: The database Entity. It holds the `startTime`, `endTime`, `startingPrice`, and `currentPrice`. Importantly, it uses a `@OneToOne` relationship to link directly to a specific `Motorcycle`.
* **`AuctionDTO.java`**: A Data Transfer Object envelope used to safely pass auction data between the database and the web browser.
* **`AuctionRepository.java`**: The Spring Data interface. It includes a custom `findByStatus(AuctionStatus)` method so our automation engine can easily grab all `SCHEDULED` or `ACTIVE` auctions from PostgreSQL.

---

## 🤖 2. The Automation Engine

The most powerful part of this module is the automated lifecycle.

1. **The Master Switch (`@EnableScheduling`)**:
   We added this annotation to `AuctionPlatformApplication.java`. It acts as the master switch, allowing Spring Boot to run background tasks like a stopwatch.
2. **The Time Bot (`@Scheduled`)**:
   Inside `AuctionService.java`, we created an `autoManageAuctions()` method and tagged it with `@Scheduled(fixedRate = 60000)`. 
   * **What it does**: Every 60,000 milliseconds (1 minute), Spring silently runs this method in the background.

### **The Status Transition Logic**
When the time bot wakes up, it checks two things:
1. **Starting Auctions**: It grabs all `SCHEDULED` auctions. If the current time is past the auction's `startTime`, it transitions the auction to `ACTIVE` and completely locks the associated Motorcycle by changing its status to `IN_AUCTION`.
2. **Ending Auctions**: It grabs all `ACTIVE` auctions. If the current time is past the auction's `endTime`, it transitions the auction to `ENDED` and permanently changes the Motorcycle's status to `SOLD`.

**Why it works this way**: This logic guarantees that motorcycles are strictly locked while being bidded on and prevents users from bidding on an auction that should be closed.

---

## 🌐 3. The API Endpoints

We built `AuctionController.java` to handle incoming web traffic for `/api/auctions`.

* 🔴 **`POST /api/auctions`**
  * **Access:** Admins Only (`@PreAuthorize("hasRole('ADMIN')")`)
  * **Action:** Takes a start time, end time, and a motorcycle ID. It verifies the motorcycle exists and creates a brand new `SCHEDULED` auction.
* 🟢 **`GET /api/auctions`**
  * **Access:** Public
  * **Action:** Asks the `AuctionService` for a list of *only* the `ACTIVE` auctions, returning them to populate the live auction gallery on the frontend.
* 🟢 **`GET /api/auctions/{id}`**
  * **Access:** Public
  * **Action:** Fetches the specific details of a single auction so a user can load the bidding page.

---

> [!TIP]
> **Why are there so many comments?**
> Every file and method created in this prompt contains plain English comments detailing exactly what the code does, why the transition logic was designed that way, how `@Scheduled` operates under the hood, and what would crash if that piece of code were deleted!
