import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import api from '../services/api';

/**
 * What this component renders and why it exists:
 * AuthContext is a global state manager for user authentication. It doesn't render visual UI; instead, it wraps our entire application.
 * It exists so that any component in our app (like a Navbar or a Protected Route) can instantly know: "Is the user logged in?" and "Who is this user?" without having to pass props down 10 levels deep.
 */
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // On app startup, check if the user already has a token in their browser storage
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        // Ensure token isn't expired
        if (decoded.exp * 1000 > Date.now()) {
          setUser({ email: decoded.sub, roles: decoded.roles });
        } else {
          localStorage.removeItem('token');
        }
      } catch (err) {
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    const { token } = response.data;
    localStorage.setItem('token', token);
    const decoded = jwtDecode(token);
    setUser({ email: decoded.sub, roles: decoded.roles });
  };

  const register = async (name, email, password, role) => {
    await api.post('/auth/register', { name, email, password, role });
    // Auto-login after successful registration
    await login(email, password);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
