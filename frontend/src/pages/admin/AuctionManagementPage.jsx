import { useState, useEffect } from 'react';
import api from '../../services/api';
import BidHistoryModal from '../../components/admin/BidHistoryModal';

/**
 * What this component renders and why it exists:
 * This is the master control table for administrators. It lists every single auction in the system and provides a high-level overview.
 * 
 * How status badges help administrators quickly understand auction state:
 * Instead of reading raw text, humans process colors much faster. Green (ACTIVE) means money is flowing. Yellow (SCHEDULED) means it's upcoming. Red/Gray (ENDED) means it's closed. Badges prevent admins from making costly mistakes.
 */
const AuctionManagementPage = () => {
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Modal State
  const [selectedAuctionId, setSelectedAuctionId] = useState(null);
  const [bidsForModal, setBidsForModal] = useState([]);

  useEffect(() => {
    const fetchAuctions = async () => {
      try {
        // Fetch ALL auctions, not just ACTIVE ones
        const response = await api.get('/auctions');
        setAuctions(response.data);
      } catch (err) {
        setError('Failed to load auctions.');
      } finally {
        setLoading(false);
      }
    };
    fetchAuctions();
  }, []);

  const openBidHistory = async (auctionId) => {
    try {
      const response = await api.get(`/auctions/${auctionId}/bids`);
      setBidsForModal(response.data);
      setSelectedAuctionId(auctionId);
    } catch (err) {
      alert("Failed to load bid history.");
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'ACTIVE': return <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Active</span>;
      case 'SCHEDULED': return <span className="bg-yellow-100 text-yellow-700 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Scheduled</span>;
      case 'ENDED': return <span className="bg-gray-200 text-gray-700 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">Ended</span>;
      default: return <span>{status}</span>;
    }
  };

  if (loading) return <div className="p-8 text-gray-500 font-bold animate-pulse">Loading Operations Table...</div>;
  if (error) return <div className="p-8 text-red-500 font-bold">{error}</div>;

  return (
    <div>
      <h1 className="text-3xl font-black text-gray-900 mb-6">Auction Operations</h1>
      
      <div className="overflow-x-auto rounded-xl shadow border border-gray-100">
        <table className="w-full text-left bg-white">
          <thead className="bg-gray-50 text-gray-600 text-sm uppercase tracking-wider">
            <tr>
              <th className="p-4 font-bold">ID</th>
              <th className="p-4 font-bold">Motorcycle ID</th>
              <th className="p-4 font-bold">Current Price</th>
              <th className="p-4 font-bold">Status</th>
              <th className="p-4 font-bold text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {auctions.map(auction => (
              <tr key={auction.id} className="hover:bg-gray-50 transition">
                <td className="p-4 font-bold text-gray-900">#{auction.id}</td>
                <td className="p-4 text-gray-600">#{auction.motorcycleId}</td>
                <td className="p-4 font-black text-brand-600">${auction.currentPrice}</td>
                <td className="p-4">{getStatusBadge(auction.status)}</td>
                <td className="p-4 text-right">
                  <button 
                    onClick={() => openBidHistory(auction.id)}
                    className="text-brand-600 hover:text-brand-800 font-bold text-sm px-4 py-2 border border-brand-200 rounded-lg hover:bg-brand-50 transition"
                  >
                    View Bids
                  </button>
                </td>
              </tr>
            ))}
            {auctions.length === 0 && (
              <tr>
                <td colSpan="5" className="p-8 text-center text-gray-500 font-medium">No auctions exist in the database.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {selectedAuctionId && (
        <BidHistoryModal 
          auctionId={selectedAuctionId} 
          bids={bidsForModal} 
          onClose={() => setSelectedAuctionId(null)} 
        />
      )}
    </div>
  );
};

export default AuctionManagementPage;
