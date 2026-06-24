import { useState, useEffect } from 'react';
import api from '../services/api';
import AuctionCard from '../components/AuctionCard';

/**
 * What this component renders and why it exists:
 * The AuctionList is the core dashboard page. It fetches the list of ACTIVE auctions from the Spring Boot backend
 * and maps over them, generating an `<AuctionCard />` for each one. 
 * This creates a beautiful, responsive grid layout for users to browse motorcycles.
 */
const AuctionList = () => {
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAuctions = async () => {
      try {
        // Remember, our JWT Interceptor in api.js automatically attaches the Auth token here!
        const response = await api.get('/auctions');
        setAuctions(response.data);
      } catch (err) {
        setError('Failed to load auctions. Please make sure the server is running.');
      } finally {
        setLoading(false);
      }
    };

    fetchAuctions();
    
    // Poll the backend every 5 seconds to automatically update prices and statuses
    const interval = setInterval(fetchAuctions, 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) return <div className="text-center mt-20 text-xl font-bold text-gray-500 animate-pulse">Loading Live Auctions...</div>;
  if (error) return <div className="text-center mt-20 text-red-500 font-bold bg-red-50 p-4 rounded-lg inline-block">{error}</div>;

  return (
    <div className="py-8">
      <div className="flex justify-between items-end mb-8">
        <div>
          <h1 className="text-4xl font-black text-white tracking-tight uppercase">Live Auctions</h1>
          <p className="text-gray-400 mt-2 font-medium">Bid on premium motorcycles instantly.</p>
        </div>
      </div>

      {auctions.length === 0 ? (
        <div className="text-center py-20 bg-dark-800 rounded-xl shadow-sm border border-dark-700">
          <p className="text-gray-400 text-lg">No active auctions at the moment. Check back later!</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {auctions.map(auction => (
            <AuctionCard key={auction.id} auction={auction} />
          ))}
        </div>
      )}
    </div>
  );
};

export default AuctionList;
