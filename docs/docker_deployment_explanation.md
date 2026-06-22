# Docker Deployment Setup

This document explains the containerization strategy for the MotoAuction platform. It details how Docker and Docker Compose work together to spin up an isolated, production-ready environment containing the Spring Boot application, PostgreSQL, and Redis.

## 1. What are Docker and Docker Compose?
**Docker** is a tool that allows you to package an application and all its dependencies (like Java, specific libraries, and configuration files) into a single, standardized unit called a **Container**. Think of a container as a miniature, lightweight virtual machine that runs perfectly on any computer, regardless of whether it's a Mac, Windows, or Linux server. It eliminates the "it works on my machine" problem.

**Docker Compose** is a tool that allows you to define and run multiple Docker containers at the same time using a single configuration file (`docker-compose.yml`). Since our application requires three separate pieces (the app, the database, and the cache), Docker Compose starts them all simultaneously and connects them together on a private virtual network.

## 2. The Multi-Stage Dockerfile
We used a "multi-stage build" in our `Dockerfile`. 
*   **Stage 1 (The Builder):** This stage acts like a development computer. It installs Maven (a heavy build tool), downloads the entire source code, and compiles the Java code into a `.jar` file.
*   **Stage 2 (The Runner):** This stage acts like a production server. It starts completely fresh with a tiny, lightweight Java Runtime Environment (JRE). It only copies the finished `.jar` file from Stage 1 and discards all the heavy Maven tools and raw source code.

**Why do this?** It makes the final Docker image drastically smaller (often reducing it from 800MB to under 200MB). A smaller image starts faster, is cheaper to host, and is more secure because it contains fewer unnecessary tools that hackers could exploit.

## 3. The Services and Their Communication
Our `docker-compose.yml` defines three "services":

1.  **`auction-postgres` (Database):** This container runs PostgreSQL. It is responsible for permanently saving user accounts, bids, and motorcycle details to the hard drive.
2.  **`auction-redis` (Cache):** This container runs Redis, an in-memory database. It is incredibly fast and is used by the application to track how often a user hits the "Bid" button, preventing spam (rate limiting).
3.  **`auction-app` (Backend):** This is our Spring Boot Java application. It contains all the business logic.

**Why they must talk to each other:** The `auction-app` is useless on its own. When a user logs in, the `app` must ask `postgres` if the password is correct. When a user bids, the `app` must ask `redis` if the user is bidding too quickly, and then tell `postgres` to save the bid. Docker Compose automatically places all three containers on a shared virtual network so they can instantly communicate using their service names (e.g., the app connects to the database using the URL `jdbc:postgresql://auction-postgres:5432/auctiondb`).

## 4. Health Checks
When you start multiple containers at once, they don't all finish booting at the same time. The Spring Boot application might start in 2 seconds, but PostgreSQL might take 10 seconds to initialize its internal databases.
If the `auction-app` tries to connect to the database before it's ready, the app will crash instantly.

**What Health Checks do:** We added `healthcheck` blocks to the `postgres` and `redis` services. These continuously ping the databases every few seconds to ask "Are you ready yet?".
In the `auction-app` service, we added a `depends_on` rule stating `condition: service_healthy`. This forces Docker to pause the startup of the Java application until PostgreSQL and Redis give a definitive "thumbs up" that they are ready to accept connections. This guarantees a smooth, crash-free startup every time.

## 5. Application Profiles (application.yml vs application-prod.yml)
Spring Boot uses "Profiles" to easily switch between different environments (like your local laptop vs. a live production server).

*   **`application.yml` (Base):** This is the default configuration. It assumes you are running the code directly on your laptop. It connects to `localhost:5432` for the database and uses default passwords.
*   **`application-prod.yml` (Production):** This file contains "overrides". When the app runs inside Docker, we activate the `prod` profile. This tells Spring Boot to stop looking for databases on `localhost` and instead use the Docker networking URLs (`auction-postgres` and `auction-redis`). It also enforces stricter logging rules (like outputting logs in JSON format) and disables certain debugging tools that shouldn't be exposed on the public internet.
