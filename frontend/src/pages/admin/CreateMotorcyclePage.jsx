import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

/**
 * What this component renders and why it exists:
 * Renders a form to add a new motorcycle to the inventory. An admin must add a motorcycle to the database BEFORE they can auction it off.
 * 
 * Why forms need validation before sending data to the backend:
 * We validate the 'startingPrice' and 'year' on the frontend to provide instant feedback to the user. 
 * If they type "-50" for a price, we stop them immediately. This saves the backend from having to process a doomed request and makes the app feel faster.
 */
const CreateMotorcyclePage = () => {
  const [formData, setFormData] = useState({
    title: '', description: '', brand: '', year: new Date().getFullYear(), startingPrice: '', imageUrls: '', status: 'AVAILABLE'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Frontend Validation
    if (parseFloat(formData.startingPrice) <= 0) {
      setError('Starting price must be greater than zero.');
      return;
    }

    try {
      const payload = {
        ...formData,
        imageUrls: formData.imageUrls.split(',').map(url => url.trim()).filter(url => url.length > 0)
      };
      
      // The JWT Interceptor automatically attaches the Admin's token here!
      await api.post('/motorcycles', payload);
      setSuccess('Motorcycle added successfully!');
      setTimeout(() => navigate('/admin/auctions/new'), 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create motorcycle.');
    }
  };

  return (
    <div>
      <h1 className="text-3xl font-black text-gray-900 mb-6">Add New Motorcycle</h1>
      {error && <div className="bg-red-50 text-red-600 p-4 rounded-lg mb-6 font-bold">{error}</div>}
      {success && <div className="bg-green-50 text-green-600 p-4 rounded-lg mb-6 font-bold">{success}</div>}

      <form onSubmit={handleSubmit} className="space-y-4 max-w-2xl">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1">Title (e.g., Ninja 400)</label>
            <input type="text" required value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
          </div>
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1">Brand</label>
            <input type="text" required value={formData.brand} onChange={e => setFormData({...formData, brand: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
          </div>
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1">Year</label>
            <input type="number" required value={formData.year} onChange={e => setFormData({...formData, year: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
          </div>
          <div>
            <label className="block text-sm font-bold text-gray-700 mb-1">Estimated Value / Starting Price ($)</label>
            <input type="number" step="0.01" required value={formData.startingPrice} onChange={e => setFormData({...formData, startingPrice: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" />
          </div>
        </div>

        <div>
          <label className="block text-sm font-bold text-gray-700 mb-1">Description</label>
          <textarea required rows="3" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500"></textarea>
        </div>

        <div>
          <label className="block text-sm font-bold text-gray-700 mb-1">Image URLs (comma-separated)</label>
          <input type="text" value={formData.imageUrls} onChange={e => setFormData({...formData, imageUrls: e.target.value})} className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-brand-500" placeholder="https://img1.jpg, https://img2.jpg..." />
        </div>

        <button type="submit" className="bg-brand-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-brand-700 transition">
          Save Motorcycle
        </button>
      </form>
    </div>
  );
};

export default CreateMotorcyclePage;
