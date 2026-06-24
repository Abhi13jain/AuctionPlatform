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
    <div className="bg-dark-800 rounded-xl shadow-xl hover:shadow-2xl hover:border-brand-500 transition duration-300 overflow-hidden border border-dark-700 flex flex-col group">
      {/* Top Banner indicating status */}
      <div className={`p-2 text-center text-sm font-bold text-white uppercase tracking-wider ${
        auction.status === 'ACTIVE' ? 'bg-green-500' : 'bg-yellow-500'
      }`}>
        {auction.status}
      </div>
      
      {auction.motorcycle && auction.motorcycle.imageUrls && auction.motorcycle.imageUrls.length > 0 && (
        <div className="relative overflow-hidden">
          <img src={auction.motorcycle.imageUrls[0]} alt="Motorcycle" className="w-full h-48 object-cover border-b border-dark-700 group-hover:scale-105 transition duration-500" />
          <div className="absolute inset-0 bg-gradient-to-t from-dark-800 to-transparent opacity-60"></div>
        </div>
      )}

      <div className="p-6 flex flex-col flex-grow">
        <h3 className="text-2xl font-display uppercase tracking-wide text-white mb-2 truncate">
          {auction.motorcycle ? `${auction.motorcycle.year} ${auction.motorcycle.brand} ${auction.motorcycle.title}` : `Motorcycle #${auction.motorcycleId}`}
        </h3>
        
        <div className="flex justify-between items-end mt-4 mb-6">
          <div>
            <p className="text-sm text-gray-400 font-medium uppercase tracking-widest">Current Price</p>
            <p className="text-3xl font-black text-brand-500 font-display tracking-wider">
              {formatPrice(auction.currentPrice)}
            </p>
          </div>
        </div>

        <div className="bg-dark-900 border border-dark-700 p-4 rounded-lg mt-auto shadow-inner">
          <p className="text-xs text-gray-500 uppercase font-bold mb-1 tracking-widest">Time Remaining</p>
          <p className={`text-lg font-black font-display tracking-widest ${timeLeft === 'Auction Ended' ? 'text-brand-600' : 'text-gray-100'}`}>
            {timeLeft}
          </p>
        </div>
        
        {auction.status === 'ACTIVE' && (
          <Link to={`/auctions/${auction.id}`} className="w-full mt-5 bg-brand-600 hover:bg-brand-500 text-white font-bold font-display tracking-widest uppercase py-4 px-4 rounded-lg transition text-center block shadow-lg shadow-brand-500/20">
            Enter Live Room
          </Link>
        )}
      </div>
    </div>
  );
};

export default AuctionCard;
