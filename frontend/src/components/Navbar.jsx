import { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

/**
 * What this component renders and why it exists:
 * The Navbar sits at the top of every single page. It provides global navigation so users can jump between Login, Register, and Auctions.
 * It changes dynamically based on whether the user is logged in (showing "Logout" instead of "Login").
 */
const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-md p-4">
      <div className="container mx-auto flex justify-between items-center">
        <Link to="/" className="text-xl font-bold text-brand-600">
          MotoAuction
        </Link>
        <div className="space-x-4">
          {user ? (
            <>
              <span className="text-gray-600">Hi, {user.email}</span>
              {user.roles && (Array.isArray(user.roles) ? user.roles.some(r => r.includes('ADMIN')) : user.roles.includes('ADMIN')) && (
                <Link to="/admin" className="bg-gray-800 text-white px-4 py-2 rounded-lg hover:bg-gray-900 transition font-bold text-sm">
                  Admin Panel
                </Link>
              )}
              <Link to="/auctions" className="text-gray-700 hover:text-brand-600 font-medium">
                Live Auctions
              </Link>
              <button 
                onClick={handleLogout} 
                className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-gray-700 hover:text-brand-600 font-medium">Login</Link>
              <Link to="/register" className="bg-brand-600 text-white px-4 py-2 rounded-lg hover:bg-brand-700 transition">
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
