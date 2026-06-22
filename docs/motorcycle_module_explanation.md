# Spring Boot Motorcycle Listings Module

This document breaks down the implementation of the **Motorcycle Module** for the Auction Platform. In this phase, we transitioned to a production-ready PostgreSQL database and introduced strict Admin-only security rules for managing the auction inventory.

---

## 🛠️ 1. Infrastructure Upgrades

Before building the new feature, we had to upgrade our foundation:

* **Migrated to PostgreSQL**: 
  * Replaced the lightweight H2 database driver in `pom.xml` with the official `org.postgresql` dependency.
  * Updated `application.properties` to connect to `jdbc:postgresql://localhost:5432/auctiondb` instead of the temporary in-memory database.
* **Activated Method Security**: Added the `@EnableMethodSecurity` annotation to `SecurityConfig.java`. This acts as a master switch that allows us to secure individual methods using annotations like `@PreAuthorize`.
* **Enabled Role Verification**: Updated `CustomUserDetailsService.java` to extract the user's `Role` (e.g., BUYER or ADMIN) from the database and translate it into a Spring Security `GrantedAuthority` (e.g., `ROLE_ADMIN`). Without this translation, Spring wouldn't know who has permission to do what.

---

## 🏍️ 2. The Motorcycle Architecture

We created a complete vertical slice of the application for Motorcycles:

### **The Database Blueprint**
* **`MotorcycleStatus.java`**: A strict Enum locking the state of a bike to `AVAILABLE`, `IN_AUCTION`, or `SOLD`. This prevents database typos and inconsistencies.
* **`Motorcycle.java`**: The Entity class marked with `@Entity`. It commands Hibernate to generate a `motorcycles` table with columns for `title`, `description`, `brand`, `year`, `startingPrice` (using precise `BigDecimal` math), and `imageUrl`.

### **The Data Handlers**
* **`MotorcycleDTO.java`**: A Data Transfer Object used as a protective envelope. Instead of exposing our raw database Entity to the internet, we pack the information into this DTO when receiving or sending data.
* **`MotorcycleRepository.java`**: Extending `JpaRepository`, this magic interface automatically generates all the complex SQL queries needed to save, delete, or fetch motorcycles from PostgreSQL.
* **`MotorcycleService.java`**: The business logic layer. While simple now, this `@Service` is where we will eventually place complex rules (like checking if an auction is currently active before allowing a deletion).

---

## 🚦 3. API Endpoints & Role-Based Security

We built `MotorcycleController.java` to act as the traffic cop for the `/api/motorcycles` URL. 

Here is how the endpoints are secured:

* 🟢 **`GET` - Fetch All Motorcycles**
  * **Access:** Public (Anyone can view)
  * **Action:** Retrieves the entire inventory from the database and returns it so users can browse the auction gallery.
* 🔴 **`POST` - Create New Listing**
  * **Access:** Admins Only (`@PreAuthorize("hasRole('ADMIN')")`)
  * **Action:** Accepts a `MotorcycleDTO`, defaults the status to `AVAILABLE`, and saves the new bike to the database.
* 🔴 **`PUT` - Update Existing Listing**
  * **Access:** Admins Only (`@PreAuthorize("hasRole('ADMIN')")`)
  * **Action:** Finds a bike by its ID and overwrites its details (useful for correcting typos or manually changing a status to `SOLD`).
* 🔴 **`DELETE` - Remove Listing**
  * **Access:** Admins Only (`@PreAuthorize("hasRole('ADMIN')")`)
  * **Action:** Permanently erases a motorcycle from the PostgreSQL database using its ID.

---

> [!NOTE] 
> **The Power of Annotations**
> Throughout the codebase, you will see beginner-friendly comments explaining exactly what annotations like `@RestController`, `@RequestBody`, and `@PathVariable` are doing behind the scenes!
