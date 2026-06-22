# Spring Boot Authentication Module Overview

This document provides a beginner-friendly breakdown of the Authentication Module created for the **Auction Platform**. We started with an empty folder and built a fully functional Spring Boot backend equipped with JWT-based security.

---

## 🏗️ What Was Created (The Architecture)

We followed a standard Spring Boot layered architecture to keep the code organized and scalable. 

### 1. Project Configuration
* **`pom.xml`**: The recipe book for the project. It tells Maven to download Spring Web, Spring Data JPA, Spring Security, the H2 in-memory database, and the JWT library (`io.jsonwebtoken`).
* **`application.properties`**: Configuration file that sets up our H2 database and tells Spring how to connect to it.
* **`AuctionPlatformApplication.java`**: The main entry point that starts the entire server.

### 2. The Database Layer (Models & Repositories)
* **`User.java`**: The database blueprint (Entity) for our users. It creates the `users` table with columns for `id`, `name`, `email`, `password`, and `role`.
* **`Role.java`**: An Enum that restricts user types to either `BUYER` or `ADMIN`.
* **`UserRepository.java`**: The magic interface that allows us to search the database (e.g., `findByEmail`) without writing any SQL queries.

### 3. Data Transfer Objects (DTOs)
DTOs are envelopes used to carry data securely between the internet and our server.
* **`RegisterRequest.java`**: Carries the name, email, and password during sign-up.
* **`AuthRequest.java`**: Carries the email and password during login.
* **`AuthResponse.java`**: Carries the newly generated JWT token back to the user after a successful login.

### 4. Security System (The Core)
* **`JwtUtil.java`**: The ID Card maker. It handles the complex math needed to generate a secure JWT and verify that incoming JWTs haven't expired or been tampered with.
* **`CustomUserDetailsService.java`**: The bridge that connects Spring Security to our specific `UserRepository` so Spring knows how to find our users.
* **`JwtAuthenticationFilter.java`**: The security guard. It intercepts every single request, looks for a JWT in the header, and logs the user in if the token is valid.
* **`SecurityConfig.java`**: The master rulebook. It disables sessions (making the app stateless), encrypts passwords using `BCryptPasswordEncoder`, and leaves the `/register` and `/login` doors unlocked while securing the rest of the application.

### 5. The API Layer (Controllers)
* **`AuthController.java`**: The receptionist. It exposes the URLs that the outside world can talk to.
  * `POST /api/auth/register`: Hashes the password, assigns the `BUYER` role, and saves the new user.
  * `POST /api/auth/login`: Checks the credentials against the database and returns a JWT if they match.

---

## 🔄 How the Authentication Flow Works

### Registration Flow
1. A user sends a `POST` request to `/api/auth/register` with their name, email, and password.
2. The `AuthController` checks if the email is taken.
3. It scrambles the password using `BCrypt`.
4. It saves the user to the database as a `BUYER`.

### Login Flow
1. A user sends a `POST` request to `/api/auth/login` with their email and password.
2. Spring Security checks the hashed password against the database.
3. If correct, the `AuthController` asks `JwtUtil` to generate a JWT.
4. The JWT is returned to the user in an `AuthResponse`.

### Accessing Protected Data
1. The user makes a request (e.g., placing a bid) and attaches their JWT to the `Authorization: Bearer <token>` header.
2. The `JwtAuthenticationFilter` catches the request.
3. It reads the JWT, verifies the signature, and checks the expiration date.
4. If valid, the filter lets the request through to its destination.

---

> [!TIP]
> **Next Steps**
> Now that the foundation is laid, you can start building features like adding `Item` or `Bid` models, and the security system will automatically protect them!
