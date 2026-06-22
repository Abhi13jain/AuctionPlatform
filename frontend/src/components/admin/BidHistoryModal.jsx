/**
 * What this component renders and why it exists:
 * A reusable popup modal that appears over the screen when an admin clicks "View Bids". 
 * 
 * How bid history helps admins monitor auction activity:
 * Admins need to audit auctions to ensure there is no bid manipulation or bots. 
 * This modal fetches the exact history of an auction so the admin can see every single user who bid, exactly what time they bid, and the progression of the price.
 */
const BidHistoryModal = ({ auctionId, onClose, bids }) => {
  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden flex flex-col max-h-[80vh]">
        <div className="p-6 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h2 className="text-xl font-black text-gray-900">Bid History (Auction #{auctionId})</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-red-500 font-bold text-xl transition">&times;</button>
        </div>
        
        <div className="p-6 overflow-y-auto flex-grow">
          {bids.length === 0 ? (
            <p className="text-center text-gray-500 font-medium py-8">No bids have been placed yet.</p>
          ) : (
            <div className="space-y-3">
              {bids.map(bid => (
                <div key={bid.id} className="flex justify-between items-center p-4 bg-white border border-gray-100 rounded-lg shadow-sm">
                  <div>
                    <p className="font-bold text-gray-900">{bid.userId}</p>
                    <p className="text-xs text-gray-500 mt-1">{new Date(bid.timestamp).toLocaleString()}</p>
                  </div>
                  <p className="font-black text-brand-600 text-lg">${bid.amount}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default BidHistoryModal;
