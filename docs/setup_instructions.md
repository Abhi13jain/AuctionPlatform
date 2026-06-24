# MotoAuction Local Setup Guide

Welcome to the MotoAuction platform! This project uses a **Spring Boot** backend with **PostgreSQL** and **Redis**, and a **React (Vite)** frontend with TailwindCSS. 

Follow these instructions to get the entire platform running on your local machine for development.

---

## 🛠️ Prerequisites
Before you begin, ensure you have the following installed on your machine:
1. **[Docker Desktop](https://www.docker.com/products/docker-desktop/)**: Required to run the backend and databases simultaneously.
2. **[Node.js](https://nodejs.org/)** (v18 or higher): Required to run the React frontend.
3. **Git**: To clone the repository.

---

## 🔴 Step 1: Starting the Backend Engine

The backend relies on three separate containers: The Spring Boot Java Application, a PostgreSQL Database, and a Redis Cache. We have bundled these together using Docker Compose for a one-click setup.

1. **Open your terminal** and navigate to the root folder of the project:
   ```bash
   cd "Auction Platform"
   ```

2. **Start the Docker cluster:**
   Run the following command to download the database images, build the Java application, and start the servers:
   ```bash
   docker-compose up --build
   ```
   *(Note: You can add `-d` to the end of the command to run it in the background).*

3. **Verify it's running:**
   Wait until the terminal stops scrolling and says something like `Started AuctionApplication`. 
   Your backend API is now running at `http://localhost:8080`.

---

## 🟢 Step 2: Starting the Frontend UI

The frontend is a modern React application built with Vite for lightning-fast hot reloading.

1. **Open a SECOND, new terminal window** (leave the backend running in the first one).
2. **Navigate to the frontend folder:**
   ```bash
   cd "Auction Platform/frontend"
   ```
3. **Install the dependencies:**
   ```bash
   npm install
   ```
4. **Start the development server:**
   ```bash
   npm run dev
   ```

---

## 🚀 Step 3: Accessing the Application

Once both the backend and frontend are running, you can access the platform!

* **Main Application:** Open your web browser and go to `http://localhost:5173`
* **Admin Access:** If you need to log in as an administrator to create auctions, use the admin credentials defined in your backend security configurations.

---

## 🛑 Shutting Down

When you are done developing for the day, you should shut down the Docker containers so they don't consume your computer's memory.

1. Go to the terminal running `docker-compose`.
2. Press `Ctrl + C` on your keyboard to stop the logs.
3. Run the teardown command:
   ```bash
   docker-compose down
   ```
This will cleanly stop and remove the containers, but your database data will be safely persisted in a Docker volume for the next time you start it!
