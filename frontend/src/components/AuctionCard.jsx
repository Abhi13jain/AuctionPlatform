import { Link } from 'react-router-dom';
import useCountdown from '../hooks/useCountdown';

/**
 * What this component renders and why it exists:
 * The AuctionCard is a reusable visual block that displays the details of a single motorcycle up for auction.
 * Instead of writing out the HTML for an auction 50 times in a list, we write it once here and pass in the specific `auction` data as a prop.
 */
const AuctionCard = ({ auction }) => {
  // We feed the target end date into our custom hook to get the live ticking string back!
  const timeLeft = useCountdown(auction.endTime);

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(price);
  };

  return (
    <div className="bg-white rounded-xl shadow-lg hover:shadow-xl transition duration-300 overflow-hidden border border-gray-100 flex flex-col">
      {/* Top Banner indicating status */}
      <div className={`p-2 text-center text-sm font-bold text-white uppercase tracking-wider ${
        auction.status === 'ACTIVE' ? 'bg-green-500' : 'bg-yellow-500'
      }`}>
        {auction.status}
      </div>

      <div className="p-6 flex flex-col flex-grow">
        <h3 className="text-xl font-extrabold text-gray-900 mb-2 truncate">
          Motorcycle ID: {auction.motorcycleId}
        </h3>
        
        <div className="flex justify-between items-end mt-4 mb-6">
          <div>
            <p className="text-sm text-gray-500 font-medium uppercase tracking-wide">Current Price</p>
            <p className="text-3xl font-black text-brand-600">
              {formatPrice(auction.currentPrice)}
            </p>
          </div>
        </div>

        <div className="bg-gray-50 p-4 rounded-lg mt-auto">
          <p className="text-sm text-gray-500 uppercase font-semibold mb-1">Time Remaining</p>
          <p className={`text-lg font-bold ${timeLeft === 'Auction Ended' ? 'text-red-500' : 'text-gray-800'}`}>
            {timeLeft}
          </p>
        </div>
        
        {auction.status === 'ACTIVE' && (
          <Link to={`/auctions/${auction.id}`} className="w-full mt-4 bg-brand-600 hover:bg-brand-700 text-white font-bold py-3 px-4 rounded-lg transition text-center block">
            Enter Live Room
          </Link>
        )}
      </div>
    </div>
  );
};

export default AuctionCard;
