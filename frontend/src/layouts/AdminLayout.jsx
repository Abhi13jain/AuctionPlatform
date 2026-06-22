import { Link, Outlet } from 'react-router-dom';

/**
 * What this component renders and why it exists:
 * The AdminLayout wraps all internal admin pages. It provides a persistent sidebar/menu specifically for administrators, allowing them to quickly jump between managing motorcycles, auctions, and users.
 */
const AdminLayout = () => {
  return (
    <div className="flex flex-col md:flex-row gap-6 my-8">
      {/* Admin Sidebar */}
      <aside className="w-full md:w-64 bg-white p-6 rounded-xl shadow border border-gray-100 flex-shrink-0 h-max">
        <h2 className="text-xl font-black text-gray-900 mb-6 uppercase tracking-wider">Admin Panel</h2>
        <nav className="flex flex-col space-y-2">
          <Link to="/admin/auctions" className="p-3 bg-gray-50 hover:bg-brand-50 hover:text-brand-600 rounded-lg font-bold text-gray-700 transition">
            Manage Auctions
          </Link>
          <Link to="/admin/motorcycles/new" className="p-3 bg-gray-50 hover:bg-brand-50 hover:text-brand-600 rounded-lg font-bold text-gray-700 transition">
            Add Motorcycle
          </Link>
          <Link to="/admin/auctions/new" className="p-3 bg-gray-50 hover:bg-brand-50 hover:text-brand-600 rounded-lg font-bold text-gray-700 transition">
            Create Auction
          </Link>
        </nav>
      </aside>

      {/* Main Admin Content Area */}
      <main className="flex-grow bg-white p-6 rounded-xl shadow border border-gray-100">
        {/* The <Outlet /> is where the specific admin page (like CreateMotorcyclePage) gets rendered */}
        <Outlet />
      </main>
    </div>
  );
};

export default AdminLayout;
