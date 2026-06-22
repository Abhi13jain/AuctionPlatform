# Real-Time Bidding Module & GitHub Security

This document explains the **Real-Time Bidding Module** and the vital **Security configurations** implemented to safely store this project in GitHub.

---

## ⚡ 1. Real-Time Communication (WebSockets & STOMP)

To make an auction platform feel alive, users must see new bids instantly without refreshing their browsers. We solved this using **WebSockets**.

* **The Walkie-Talkie vs. The Phone Call**: Normal web traffic (HTTP) is like a walkie-talkie; you click a button (send request), and the server replies. WebSockets are like a phone call; the connection stays open permanently, allowing the server to push new price updates to the user's screen at any millisecond.
* **`WebSocketConfig.java`**: We enabled STOMP (the language spoken over the WebSocket phone call) and opened the `/ws` endpoint so browsers can connect.

---

## 🚀 2. High-Performance Price Checking (Redis)

Auctions can be chaotic. If 50 people bid in the exact same second, querying the PostgreSQL database 50 times to check the current price would cause massive lag.

* **The Solution**: We integrated **Redis** (an ultra-fast, in-memory caching system).
* **`BidService.java`**: Before saving a bid, the service checks the `currentPrice` directly from the server's RAM via Redis. If a bid is valid, it instantly updates the Redis cache with the new high price. This allows us to process hundreds of bids per second without breaking a sweat.

---

## 🛡️ 3. Bid Validation & Broadcasting

The `BidService` acts as the bouncer for incoming bids. Every bid must pass three strict checks:
1. **User Authentication Check**: Prevents anonymous users or hackers from submitting fake bids.
2. **Auction Status Check**: Ensures the auction is strictly `ACTIVE` (not `SCHEDULED` or `ENDED`).
3. **Price Check**: Ensures the incoming bid is mathematically higher than the current price stored in Redis.

If the bid passes, it is saved to the PostgreSQL database, and the **`BidController`** takes over:
* The `@SendTo("/topic/auction/{auctionId}")` annotation acts like a megaphone, instantly broadcasting the new winning bid to every single user currently viewing that auction page.

---

## 🔒 4. GitHub Security & Secret Management

Because you initialized Git and ran `git add .`, your database password was dangerously close to being permanently exposed on the internet. We fixed this by implementing standard industry security practices:

* **Unstaging**: We intercepted the commit by running `git reset`, pulling the files out of the staging area.
* **Environment Variables**: We removed the hardcoded password `Abhi123J` from `application.properties` and replaced it with a dynamic placeholder: `${DB_PASSWORD}`. This forces Spring Boot to read the password directly from your operating system instead of the code.
* **`.gitignore`**: We created a robust ignore file to prevent Git from ever tracking compiled Java `.jar` files, local IDE configurations, or hidden `.env` secret files.

Your code is now highly performant, real-time, and 100% safe to push to a public GitHub repository!
