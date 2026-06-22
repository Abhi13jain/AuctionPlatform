# MotoAuction - Real-Time Bike Auction Platform

## 1. System Overview and Goals
MotoAuction is a full-stack web application designed to facilitate real-time bidding on motorcycles. The primary goal of the system is to provide a seamless, instantly responsive experience for users attempting to outbid each other in the final seconds of an auction, while providing administrators with a secure dashboard to manage inventory and scheduled events.

To achieve this, the platform is split into two independent pieces: a React frontend (the visual interface users interact with) and a Spring Boot backend (the engine that securely processes logic and saves data).

## 2. Architecture Diagram Description
The backend is structured using a traditional "N-Tier" architecture, which simply means the code is organized into distinct layers, each with a specific responsibility:

*   **API Layer (Controllers):** This is the front door of the backend. It receives incoming web requests from the React frontend, validates that the data looks correct, and passes it down to the Service layer.
*   **Service Layer:** This is the brain of the application. It contains all the business rules. For example, before accepting a bid, the Service layer checks if the auction is actually open and if the new bid is higher than the current highest bid.
*   **Repository Layer:** This layer is responsible solely for talking to the database. It handles the raw SQL (Structured Query Language, the language used to talk to databases) needed to save or retrieve information.
*   **Database Layer:** We use PostgreSQL, a robust relational database, to permanently store users, motorcycles, and auction data. We also use Redis, an in-memory storage system that is incredibly fast, to quickly check if a user is clicking "Bid" too many times in a row.

## 3. Key Design Decisions and Trade-offs

*   **Stateless Authentication with JWT:** Instead of the server remembering who is logged in (which takes up server memory), we use JSON Web Tokens (JWT). A JWT is a digital ID card given to the user upon login. The user presents this card with every request.
    *   *Trade-off:* It makes the server incredibly lightweight and easy to scale, but revoking a token before it expires is difficult because the server doesn't keep a master list of active tokens.
*   **Optimistic UI Updates:** When a user places a bid on the frontend, the UI instantly updates to show their bid as the highest, *before* the server confirms it.
    *   *Trade-off:* It makes the app feel lightning-fast. However, if the server rejects the bid (e.g., someone else bid a millisecond faster), the frontend has to "roll back" the visual change and show an error.
*   **Rate Limiting with Redis:** To prevent malicious users from spamming the "Bid" button and crashing the server, we use Redis and a library called Bucket4j.
    *   *Trade-off:* It adds a slight layer of complexity to the infrastructure by requiring a Redis container, but it is mandatory to ensure the platform doesn't go down during a highly contested auction.

## 4. WebSocket Flow for Real-Time Bidding
WebSockets provide a persistent, two-way connection between the user's browser and the server, unlike standard web traffic where the browser has to constantly ask "Are there any updates?". 

Here is how a bid works in real-time:
1.  **Connection:** When a user opens an auction page, their browser establishes a WebSocket connection and "subscribes" to a specific channel (e.g., `/topic/auction/5`).
2.  **Action:** The user types $500 and clicks "Place Bid". This request is sent to the server.
3.  **Validation:** The server verifies the bid is valid and saves it to the PostgreSQL database.
4.  **Broadcast:** The server instantly shouts the new $500 price down the WebSocket channel `/topic/auction/5`.
5.  **Update:** Every single user who has that auction page open receives the broadcast instantly, and their screens update to show the new price without anyone having to refresh their page.

## 5. Entity Relationship Summary
The database is structured around four core entities (tables):
*   **User:** Represents individuals using the platform. Users can be a `BUYER` or an `ADMIN`.
*   **Motorcycle:** Represents the physical item being sold. Contains details like brand, year, and images.
*   **Auction:** Represents the event of selling a motorcycle. It links to a specific Motorcycle and has a start time, end time, and current status (e.g., `SCHEDULED`, `ACTIVE`, `ENDED`).
*   **Bid:** Represents an offer made by a User during an Auction. It links a specific User to a specific Auction, recording the exact monetary amount and the exact millisecond it was placed.

---

## Setup & Execution Instructions

### Prerequisites
*   **Docker Desktop** installed and running on your machine.
*   **Node.js** (v18+) installed (only if you wish to run the frontend independently).

### Environment Variables
The project uses environment variables (hidden configuration values) to manage sensitive data. 

Create a `.env` file inside the `/frontend` directory with the following:
```env
VITE_API_URL=http://localhost:8080
```

### How to Run with Docker
The entire system (Backend, Database, Redis) is containerized, meaning it runs inside isolated virtual environments that have all their dependencies pre-installed.

1. Open a terminal in the root folder of the project.
2. Run the following command:
   ```bash
   docker-compose up -d --build
   ```
3. Docker will automatically download PostgreSQL, Redis, and Java, build the Spring Boot application, and start all services.
4. The Spring Boot backend will be accessible at `http://localhost:8080`.

**To run the React Frontend locally during development:**
1. Open a new terminal in the `/frontend` folder.
2. Run `npm install` to download required libraries.
3. Run `npm run dev` to start the development server.
4. Open your browser to `http://localhost:3000`.

### Core API Endpoints

**Authentication:**
*   `POST /api/auth/register` - Creates a new user account.
*   `POST /api/auth/login` - Authenticates a user and returns a JWT.

**Auctions (Public/Buyer):**
*   `GET /api/auctions` - Retrieves a list of active and scheduled auctions.
*   `GET /api/auctions/{id}` - Retrieves full details of a specific auction.
*   `GET /api/auctions/{id}/bids` - Retrieves the history of bids for an auction.
*   `POST /api/auctions/{id}/bid` - Submits a new bid (Requires Authentication).

**Admin Dashboard (Requires ADMIN role):**
*   `POST /api/motorcycles` - Adds a new motorcycle to inventory.
*   `POST /api/auctions` - Schedules a new auction.

### Assumptions Made
*   **Timezones:** The server operates on UTC time to prevent issues when users from different timezones bid on the same item.
*   **Image Storage:** Currently, the system assumes image URLs are provided as direct web links (e.g., from an AWS S3 bucket or Imgur) rather than handling direct file uploads to the database.
*   **Payment Processing:** The system handles the bidding war but assumes that the actual exchange of money (e.g., Stripe integration) occurs offline or in a separate unbuilt module once an auction successfully concludes.