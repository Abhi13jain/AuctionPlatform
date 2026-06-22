import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

/**
 * What a protected route is and what happens when a logged-out user tries to access it:
 * Normally, anyone can type "/auctions" into their browser URL bar to view a page.
 * A Protected Route wraps around sensitive pages (like "Place a Bid"). 
 * If a user tries to access the page, this component intercepts them, checks the `AuthContext` to see if they are logged in.
 * If they are NOT logged in, it instantly forces their browser to redirect to the `/login` page.
 */
const ProtectedRoute = ({ children }) => {
  const { user } = useContext(AuthContext);

  if (!user) {
    // Navigate acts as an instant redirect. 'replace' means they can't hit the back button to bypass it.
    return <Navigate to="/login" replace />;
  }

  // If they are logged in, simply render the page they originally requested
  return children;
};

export default ProtectedRoute;
