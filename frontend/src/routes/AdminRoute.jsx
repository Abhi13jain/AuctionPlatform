import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

/**
 * How the app reads the admin role from the JWT token:
 * When the user logs in, the AuthContext decodes the JWT token (which is just a Base64 string). The payload of the token contains a "roles" array or string injected by Spring Boot.
 * 
 * Why JWT role checks are performed on the frontend:
 * We do this purely for User Experience (UX). If a user is just a Buyer, we don't want to show them the Admin Dashboard button because clicking it would just result in a frustrating error.
 * 
 * Why backend authorization is still required even if frontend checks exist:
 * FRONTEND CHECKS ARE NOT SECURE! A hacker could easily open Chrome Developer Tools and change `user.roles = 'ADMIN'` in the Javascript memory. 
 * However, when they actually try to send a request to the Spring Boot backend to create an auction, the backend verifies the cryptographic signature of the JWT token. 
 * If the hacker changed their role, the signature becomes invalid, and Spring Boot instantly blocks them with a 403 Forbidden.
 * 
 * What happens when a non-admin tries to access admin routes:
 * This component intercepts them and instantly redirects them back to the normal /auctions list, preventing them from even loading the Admin UI.
 */
const AdminRoute = ({ children }) => {
  const { user } = useContext(AuthContext);

  // Check if user exists and if their roles string/array includes 'ADMIN' or 'ROLE_ADMIN'
  const isAdmin = user && user.roles && (
    Array.isArray(user.roles) 
      ? user.roles.some(role => role.includes('ADMIN'))
      : typeof user.roles === 'string' && user.roles.includes('ADMIN')
  );

  if (!isAdmin) {
    return <Navigate to="/auctions" replace />;
  }

  return children;
};

export default AdminRoute;
