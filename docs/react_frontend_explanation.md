# React Frontend Architecture Guide

This document explains the technical implementation of the React frontend we built for the Spring Boot Auction Platform. It was designed with a modern, production-ready architecture using React 18, Vite, and Tailwind CSS.

---

## 🚀 Tech Stack Breakdown

*   **React + Vite**: We chose Vite instead of the older `create-react-app`. Vite provides lightning-fast hot module replacement (HMR), meaning when you save a file, the browser updates instantly. It also results in a much smaller, faster build.
*   **React Router DOM**: Used to create a Single Page Application (SPA). Instead of asking the server for a new HTML page every time the user clicks a link, React Router swaps out the components instantly in the browser.
*   **Axios**: A powerful HTTP client used instead of the native `fetch` API because it allows us to easily attach "Interceptors" (more on this below).
*   **Tailwind CSS**: A utility-first CSS framework. It allowed us to rapidly build a beautiful, responsive, and mobile-friendly UI without writing dozens of custom CSS files.

---

## 📁 Clean Folder Structure

We implemented a highly organized folder structure to ensure the project scales cleanly:

*   **`src/pages`**: Contains the full-screen views (e.g., `Login.jsx`, `Register.jsx`, `AuctionList.jsx`).
*   **`src/components`**: Contains reusable UI pieces that don't represent a whole page (e.g., `Navbar.jsx`, `AuctionCard.jsx`).
*   **`src/hooks`**: Contains custom React hooks (e.g., `useCountdown.js`) to extract complex math or logic away from the UI components.
*   **`src/services`**: Contains our API configurations (e.g., `api.js`).
*   **`src/routes`**: Contains security wrappers for our routing (e.g., `ProtectedRoute.jsx`).
*   **`src/context`**: Contains global state managers (e.g., `AuthContext.jsx`).

---

## 🔐 Security & Authentication

Handling login in a modern web app requires several moving parts:

### 1. `AuthContext.jsx` (Global State)
Instead of passing user data down through 10 layers of components, `AuthContext` acts as a global "brain". 
*   When a user logs in, `AuthContext` takes the JWT token, decodes it (using `jwt-decode`) to find out who the user is, and saves the token to the browser's `localStorage`.
*   Now, any component (like the Navbar) can instantly ask the `AuthContext`: *"Is the user logged in?"* to decide whether to show the "Logout" button.

### 2. `api.js` (The JWT Interceptor)
When the frontend wants to place a bid, it must prove to the Spring Boot backend who the user is. 
*   **The Problem**: Manually adding the JWT token to every single API call is repetitive and prone to errors.
*   **The Solution**: We configured an Axios **Request Interceptor**. It acts like a bouncer at the door. Before *any* HTTP request leaves the browser, this interceptor automatically grabs the JWT token from `localStorage` and slaps it onto the `Authorization` header.

### 3. `ProtectedRoute.jsx`
*   If a logged-out user tries to type `http://localhost:3000/auctions` directly into their URL bar, `ProtectedRoute` intercepts them. 
*   It checks the `AuthContext` and instantly redirects them back to the `/login` page using React Router's `<Navigate>` component, keeping our internal pages safe.

---

## ⏱️ The Live Countdown Timer

Auctions require urgency. We implemented a live ticking clock on the `AuctionCard`.

### `useCountdown.js`
We created a custom hook to handle the complex math of time calculation:
1.  It takes the `endTime` of the auction (passed from Spring Boot).
2.  It creates a `setInterval` loop that triggers every `1000ms` (1 second).
3.  Every second, it subtracts the current browser time from the `endTime`.
4.  It converts the remaining milliseconds into Days, Hours, Minutes, and Seconds.
5.  By updating the React State every second, it forces the `AuctionCard` to visually re-render, creating the illusion of a ticking live clock!

---

## 🔌 Running the Frontend

The frontend uses environment variables to find the backend. 
1.  **`.env`**: Contains `VITE_API_URL=http://localhost:8080/api`
2.  **To start the server**: Run `npm run dev`. Vite will host the application locally at `http://localhost:3000`.
