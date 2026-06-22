import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import AuctionList from './pages/AuctionList';
import LiveAuctionRoom from './pages/LiveAuctionRoom';
import AdminRoute from './routes/AdminRoute';
import AdminLayout from './layouts/AdminLayout';
import CreateMotorcyclePage from './pages/admin/CreateMotorcyclePage';
import CreateAuctionPage from './pages/admin/CreateAuctionPage';
import AuctionManagementPage from './pages/admin/AuctionManagementPage';

/**
 * What this component renders and why it exists:
 * App.jsx is the spine of the React application. It defines the "Routes" (URLs) that users can visit.
 * By wrapping everything in `<AuthProvider>`, it ensures every page has access to the global login state.
 */
function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Navbar />
          <main className="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              
              {/* Protected Routes - Only accessible if logged in! */}
              <Route path="/auctions" element={
                <ProtectedRoute>
                  <AuctionList />
                </ProtectedRoute>
              } />
              
              <Route path="/auctions/:id" element={
                <ProtectedRoute>
                  <LiveAuctionRoom />
                </ProtectedRoute>
              } />

              {/* Secure Admin Routes */}
              <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
                <Route path="auctions" element={<AuctionManagementPage />} />
                <Route path="motorcycles/new" element={<CreateMotorcyclePage />} />
                <Route path="auctions/new" element={<CreateAuctionPage />} />
                {/* Redirect /admin to /admin/auctions */}
                <Route index element={<Navigate to="/admin/auctions" replace />} />
              </Route>

              {/* Redirect any unknown route to login */}
              <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
