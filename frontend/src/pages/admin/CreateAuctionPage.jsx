import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

/**
 * What this component renders and why it exists:
 * Renders a form specifically to schedule a new live auction. It fetches the existing inventory of motorcycles and lets the admin attach one to a time-based auction.
 */
const CreateAuctionPage = () => {
  const [motorcycles, setMotorcycles] = useState([]);
  const [formData, setFormData] = useState({
    motorcycleId: '', startTime: '', endTime: '', startingPrice: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch all motorcycles so the admin can select one from a dropdown
    const fetchMotorcycles = async () => {
      try {
        const response = await api.get('/motorcycles');
        // Filter to only show motorcycles that are currently AVAILABLE (not already sold or in an auction)
        setMotorcycles(response.data.filter(m => m.status === 'AVAILABLE'));
      } catch (err) {
        setError('Failed to load inventory.');
      }
    };
    fetchMotorcycles();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    // Convert to ISO strings for Spring Boot
    const payload = {
      ...formData,
      startTime: new Date(formData.startTime).toISOString(),
      endTime: new Date(formData.endTime).toISOString()
    };

    if (new Date(payload.startTime) >= new Date(payload.endTime)) {
      setError('End time must be completely after start time.');
      return;
    }

    try {
      await api.post('/auctions', payload);
      setSuccess('Auction Scheduled Successfully!');
      setTimeout(() => navigate('/admin/auctions'), 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to schedule auction.');
    }
  };

  return (
    <div>
      <h1 className="text-3xl font-black text-gray-900 mb-6">Schedule Auction</h1>
      {error && <div className="bg-red-50 text-red-600 p-4 rounded-lg mb-6 font-bold">{error}</div>}
      {success && <div className="bg-green-50 text-green-600 p-4 rounded-lg mb-6 font-bold">{success}</div>}

      <form onSubmit={handleSubmit} className="space-y-4 max-w-2xl">
        <div>
          <label className="block text-sm font-bold text-gray-700 mb-1">Select Motorcycle</label>
          <select required value={formData.motorcycleId} onChange={e => setFormData({...formData, motorcycleId: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500">
            <option value="">-- Choose from Inventory --</option>
            {motorcycles.map(m => (
              <option key={m.id} value={m.id}>#{m.id} - {m.year} {m.brand} {m.title}</option>
            ))}
          </select>
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1">Start Time</label>
            <input type="datetime-local" required value={formData.startTime} onChange={e => setFormData({...formData, startTime: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
          </div>
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1">End Time</label>
            <input type="datetime-local" required value={formData.endTime} onChange={e => setFormData({...formData, endTime: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
          </div>
        </div>

        <div>
          <label className="block text-sm font-bold text-gray-700 mb-1">Opening Bid ($)</label>
          <input type="number" step="0.01" required value={formData.startingPrice} onChange={e => setFormData({...formData, startingPrice: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
        </div>

        <button type="submit" className="bg-brand-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-brand-700 transition">
          Launch Auction
        </button>
      </form>
    </div>
  );
};

export default CreateAuctionPage;
