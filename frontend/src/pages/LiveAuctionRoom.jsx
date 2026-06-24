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

  const handleIncomingMessage = (data) => {
    // If the backend broadcasted an updated Auction state
    if (data.currentPrice !== undefined) {
      setAuction(data);
    } 
    // If the backend broadcasted a new Bid event
    else if (data.amount !== undefined) {
      setBids(prev => {
        // Prevent duplicates caused by React StrictMode double-subscriptions
        const isDuplicate = prev.some(b => b.amount === data.amount && b.userEmail === data.userEmail);
        if (isDuplicate) return prev;
        return [data, ...prev];
      });
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
    // We do NOT optimistically update the bids array here, because the WebSocket will instantly broadcast it back to us.
    // If we update it here AND via the WebSocket, it will appear twice!
    
    // Send it through the websocket
    placeBid(amount, user.email);
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
        <div className="bg-dark-800 p-6 rounded-xl shadow-2xl border border-dark-700 flex flex-col relative overflow-hidden">
          {/* Subtle gradient background for premium feel */}
          <div className="absolute top-0 right-0 -mr-20 -mt-20 w-64 h-64 rounded-full bg-brand-500 opacity-5 blur-[100px]"></div>
          <div className="flex justify-between items-center mb-4">
            <h1 className="text-3xl font-display uppercase tracking-wider text-white truncate z-10">
              {auction.motorcycle ? `${auction.motorcycle.year} ${auction.motorcycle.brand} ${auction.motorcycle.title}` : `Motorcycle #${auction.motorcycleId}`}
            </h1>
            <span className={`px-3 py-1 rounded-sm text-xs font-bold uppercase tracking-widest z-10 ${connectionStatus === 'Connected' ? 'bg-green-500/10 text-green-400 border border-green-500/20' : 'bg-red-500/10 text-red-400 border border-red-500/20'}`}>
              {connectionStatus}
            </span>
          </div>

          {auction.motorcycle && auction.motorcycle.imageUrls && auction.motorcycle.imageUrls.length > 0 && (
            <div className="flex overflow-x-auto gap-4 mb-6 pb-2 snap-x z-10 scrollbar-hide">
              {auction.motorcycle.imageUrls.map((url, i) => (
                <img key={i} src={url} alt={`Motorcycle ${i}`} className="w-full h-72 object-cover rounded-lg shadow-lg border border-dark-700 flex-shrink-0 snap-center" />
              ))}
            </div>
          )}
          {auction.motorcycle && auction.motorcycle.description && (
            <p className="text-gray-400 mb-8 text-sm leading-relaxed z-10">{auction.motorcycle.description}</p>
          )}

          <div className="bg-dark-900/50 border border-dark-700 p-6 rounded-xl mb-6 text-center z-10 shadow-inner">
            <p className="text-xs font-bold text-gray-500 uppercase tracking-[0.2em] mb-2">Current Bid</p>
            <p className="text-6xl font-display font-black text-brand-500 tracking-wider">${auction.currentPrice}</p>
          </div>

          <div className="bg-brand-600 text-white p-6 rounded-xl text-center mb-8 z-10 shadow-lg shadow-brand-500/20">
            <p className="text-xs font-bold text-brand-100 uppercase tracking-[0.2em] mb-2">Time Remaining</p>
            <p className={`text-5xl font-display font-black tracking-widest ${isEnded ? 'text-dark-900' : 'text-white'}`}>{timeLeft}</p>
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
                className="flex-grow px-6 py-4 text-xl font-display tracking-wider bg-dark-900 border border-dark-600 rounded-lg text-white focus:ring-2 focus:ring-brand-500 focus:border-brand-500 outline-none transition disabled:bg-dark-800 disabled:cursor-not-allowed placeholder-gray-600"
              />
              <button 
                type="submit" 
                disabled={isEnded || connectionStatus !== 'Connected'}
                className="bg-brand-600 text-white font-display font-bold px-10 py-4 text-xl rounded-lg hover:bg-brand-500 transition disabled:bg-dark-600 disabled:text-gray-400 disabled:cursor-not-allowed uppercase tracking-widest shadow-lg shadow-brand-500/20"
              >
                Bid
              </button>
            </div>
          </form>
        </div>

        {/* Right Column: Bid History */}
        <div className="bg-dark-800 p-6 rounded-xl shadow-2xl border border-dark-700 flex flex-col max-h-[700px]">
          <h2 className="text-xl font-display font-bold text-white mb-6 uppercase tracking-widest border-b border-dark-700 pb-4">Live Activity</h2>
          <div className="overflow-y-auto flex-grow space-y-3 pr-2 scrollbar-thin scrollbar-thumb-dark-600 scrollbar-track-transparent">
            {bids.length === 0 ? (
              <p className="text-gray-500 text-center py-10 font-medium">No bids yet. Set the pace!</p>
            ) : (
              bids.map((bid, index) => (
                <div key={bid.id || index} className="flex justify-between items-center p-4 bg-dark-900/50 rounded-lg border border-dark-700 hover:border-brand-500/50 transition group">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-sm bg-brand-500/10 text-brand-500 flex items-center justify-center font-display font-bold text-lg border border-brand-500/20">
                      {bid.userEmail?.charAt(0).toUpperCase() || '?'}
                    </div>
                    <span className="font-medium text-gray-300 truncate max-w-[120px]">{bid.userEmail || 'Unknown'}</span>
                  </div>
                  <span className="font-black font-display text-white text-xl tracking-wider">${bid.amount}</span>
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
