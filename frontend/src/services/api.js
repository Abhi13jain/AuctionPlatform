import axios from 'axios';

/**
 * What this file does and why it exists:
 * This creates a pre-configured instance of Axios (a tool for making HTTP requests). 
 * Instead of typing out the full backend URL (`http://localhost:8080/api/auctions`) every single time, we just use this `api` object.
 * 
 * What the JWT interceptor does and why every API call needs it:
 * When a user logs in, the backend gives them a "VIP Pass" (a JWT token). 
 * The interceptor is like a bouncer at the door of our application. Before ANY request leaves the browser to go to the backend, 
 * this interceptor automatically intercepts it, digs into localStorage, grabs the VIP Pass, and slaps it onto the request's Authorization header.
 * Without this, the backend would reject requests to protected routes (like creating a bid) with a 401 Unauthorized error.
 */

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

// Request Interceptor: Automatically attach the token to every outgoing request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
