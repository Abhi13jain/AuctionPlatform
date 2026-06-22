import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

/**
 * What this component renders and why it exists:
 * The Login page renders a form for existing users to enter their credentials.
 * It contains form validation to ensure users don't submit empty fields, saving the backend from unnecessary processing.
 */
const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevents the browser from instantly reloading the page on submit
    setError('');

    // Basic Form Validation
    if (!email || !password) {
      setError('Both fields are required');
      return;
    }

    try {
      await login(email, password);
      // Once the JWT is safely saved to localStorage, we teleport the user to the secure auctions page
      navigate('/auctions');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials. Please try again.');
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center">
      <div className="bg-white p-8 rounded-xl shadow-lg max-w-md w-full border border-gray-100">
        <h2 className="text-3xl font-black text-center text-gray-800 mb-6">Welcome Back</h2>
        {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm mb-4 border border-red-100">{error}</div>}
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">Email Address</label>
            <input 
              type="email" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-500 focus:border-brand-500 outline-none transition"
              placeholder="you@example.com"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-1">Password</label>
            <input 
              type="password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-brand-500 focus:border-brand-500 outline-none transition"
              placeholder="••••••••"
            />
          </div>
          <button 
            type="submit" 
            className="w-full bg-brand-600 text-white font-bold py-3 rounded-lg hover:bg-brand-700 transition duration-300 shadow-md hover:shadow-lg"
          >
            Sign In
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
