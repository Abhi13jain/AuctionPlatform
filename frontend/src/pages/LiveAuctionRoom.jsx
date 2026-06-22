import { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';
import useAuctionSocket from '../hooks/useAuctionSocket';
import useCountdown from '../hooks/useCountdown';

/**
 * What this component renders and why it exists:
 * This is the Live Bidding Room. It represents a single auction where a user can view real-time price changes, see the ticking clock, and submit bids.
 * It exists to create the high-adrenaline, real-time experience of a physical auction house, directly in the browser.
 */
const LiveAuctionRoom = () => {
  const { id } = useParams();
  const { user } = useContext(AuthContext);
  
  const [auction, setAuction] = useState(null);
  const [bids, setBids] = useState([]);
  const [bidAmount, setBidAmount] = useState('');
  const [error, setError] = useState('');

  // 1. Initial Load: Fetch the current state from the database
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [auctionRes, bidsRes] = await Promise.all([
          api.get(`/auctions/${id}`),
          api.get(`/auctions/${id}/bids`)
        ]);
        setAuction(auctionRes.data);
        setBids(bidsRes.data);
      } catch (err) {
        setError('Failed to load auction data. It might not exist.');
      }
    };
    fetchInitialData();
  }, [id]);

  // 2. Real-Time Socket Connection
  const handleIncomingMessage = (data) => {
    // If the backend broadcasted an updated Auction state
    if (data.currentPrice !== undefined) {
      setAuction(data);
    } 
    // If the backend broadcasted a new Bid event
    else if (data.amount !== undefined) {
      setBids(prev => [data, ...prev]);
      setAuction(prev => ({ ...prev, currentPrice: data.amount }));
    }
  };

  const { connectionStatus, placeBid } = useAuctionSocket(id, localStorage.getItem('token'), handleIncomingMessage);

  // 3. Live Countdown Hook
  const timeLeft = useCountdown(auction?.endTime);

  const handleBidSubmit = (e) => {
    e.preventDefault();
    setError('');
    
    const amount = parseFloat(bidAmount);
    if (!amount || amount <= auction.currentPrice) {
      setError(`Bid must be strictly greater than ${auction.currentPrice}`);
      return;
    }

    /**
     * What optimistic UI updates are and why they make the app feel faster:
     * Instead of waiting 200ms for the server to validate and broadcast the new price back to us, 
     * we INSTANTLY update the screen to show the user's bid. It makes the app feel lightning fast.
     * If the server later rejects the bid (e.g., someone else bid higher a millisecond before), the WebSocket will overwrite this with the real truth.
     */
    setAuction(prev => ({ ...prev, currentPrice: amount }));
    setBids(prev => [{ id: 'temp', amount, userId: user.email, timestamp: new Date().toISOString() }, ...prev]);
    
    // Send it through the websocket
    placeBid(amount);
    setBidAmount('');
  };

  if (!auction) return <div className="p-8 text-center text-gray-500 font-bold">Loading Live Room...</div>;

  const isEnded = auction.status === 'ENDED' || timeLeft === 'Auction Ended';

  return (
    <div className="py-8 max-w-4xl mx-auto">
      {/* 
        How the winner banner knows the auction has ended: 
        We check if the status returned by the server is 'ENDED', OR if our local clock counted down to zero.
      */}
      {isEnded && (
        <div className="bg-yellow-500 text-white p-6 rounded-xl shadow-lg text-center mb-8 animate-pulse">
          <h2 className="text-3xl font-black uppercase tracking-widest">Auction Ended!</h2>
          <p className="text-lg font-medium mt-1">Winning Price: ${auction.currentPrice}</p>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Left Column: Details */}
        <div className="bg-white p-6 rounded-xl shadow border border-gray-100 flex flex-col">
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-2xl font-black text-gray-900 truncate">Motorcycle #{auction.motorcycleId}</h1>
            <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${connectionStatus === 'Connected' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
              {connectionStatus}
            </span>
          </div>

          <div className="bg-gray-50 p-6 rounded-xl mb-6 text-center">
            <p className="text-sm font-semibold text-gray-500 uppercase tracking-widest mb-2">Current Bid</p>
            <p className="text-5xl font-black text-brand-600">${auction.currentPrice}</p>
          </div>

          <div className="bg-gray-900 text-white p-6 rounded-xl text-center mb-6">
            <p className="text-sm font-semibold text-gray-400 uppercase tracking-widest mb-2">Time Remaining</p>
            <p className={`text-4xl font-black font-mono ${isEnded ? 'text-red-500' : 'text-white'}`}>{timeLeft}</p>
          </div>

          {/* Bid Form */}
          <form onSubmit={handleBidSubmit} className="mt-auto">
            {error && <p className="text-red-500 text-sm font-bold mb-2 bg-red-50 p-2 rounded">{error}</p>}
            <div className="flex gap-2">
              <input 
                type="number" 
                min={auction.currentPrice + 1}
                step="0.01"
                value={bidAmount}
                onChange={(e) => setBidAmount(e.target.value)}
                disabled={isEnded}
                placeholder={`> $${auction.currentPrice}`}
                className="flex-grow px-4 py-3 text-lg border-2 border-gray-300 rounded-lg focus:ring-4 focus:ring-brand-500/20 focus:border-brand-500 outline-none transition disabled:bg-gray-100 disabled:cursor-not-allowed"
              />
              <button 
                type="submit" 
                disabled={isEnded || connectionStatus !== 'Connected'}
                className="bg-brand-600 text-white font-bold px-8 py-3 rounded-lg hover:bg-brand-700 transition disabled:bg-gray-400 disabled:cursor-not-allowed uppercase tracking-wider"
              >
                Bid
              </button>
            </div>
          </form>
        </div>

        {/* Right Column: Bid History */}
        <div className="bg-white p-6 rounded-xl shadow border border-gray-100 flex flex-col max-h-[600px]">
          <h2 className="text-xl font-bold text-gray-900 mb-4 border-b pb-2">Live Activity</h2>
          <div className="overflow-y-auto flex-grow space-y-3 pr-2">
            {bids.length === 0 ? (
              <p className="text-gray-500 text-center py-10 font-medium">No bids yet. Be the first!</p>
            ) : (
              bids.map((bid, index) => (
                <div key={bid.id || index} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg border border-gray-100 hover:border-brand-200 transition">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-brand-100 text-brand-600 flex items-center justify-center font-bold text-sm">
                      {bid.userId?.charAt(0).toUpperCase() || '?'}
                    </div>
                    <span className="font-medium text-gray-700 truncate max-w-[120px]">{bid.userId || 'Unknown'}</span>
                  </div>
                  <span className="font-black text-gray-900 text-lg">${bid.amount}</span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default LiveAuctionRoom;
