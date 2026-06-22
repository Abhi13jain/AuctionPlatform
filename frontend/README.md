# React + Vite Frontend for Auction Platform

This is the fully functioning React frontend built to connect to the Spring Boot backend.

## Tech Stack
- **React 18** + **Vite** (Lightning fast build tool)
- **React Router DOM** (Client-side routing)
- **Axios** (HTTP client with JWT interceptors)
- **Tailwind CSS** (Utility-first styling for a beautiful, responsive UI)

## Folder Structure
- `src/components`: Reusable UI parts (Navbar, AuctionCard)
- `src/pages`: Full page views (Login, Register, AuctionList)
- `src/context`: Global state management (AuthContext for JWT sessions)
- `src/hooks`: Custom React hooks (like the `useCountdown` live timer)
- `src/routes`: Security wrappers (ProtectedRoute)
- `src/services`: API wrappers (Axios instance with Interceptors)

## How to Run

1. Open a terminal and navigate to this `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install the exact dependencies needed:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open your browser to `http://localhost:3000`.

*(Make sure your Spring Boot backend Docker containers are running so the frontend can fetch the live data!)*
