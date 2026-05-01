import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const ProfilePage = () => {
  const { user, loading, initializing, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (initializing || loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#f3f4f6]">
        <p className="text-[#4b5563] text-sm font-medium">Loading...</p>
      </div>
    );
  }

  if (!user) return null;

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#f3f4f6] px-4">
      <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-xl text-center">

        <div
          className="w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-4
                     text-white text-3xl font-bold shadow-md"
          style={{ background: 'linear-gradient(135deg, #486881, #19135f)' }}
        >
          {user.username?.charAt(0).toUpperCase()}
        </div>

        <h1 className="text-2xl font-semibold text-[#111827] mb-1">{user.username}</h1>
        <p className="text-sm text-[#6b7280] mb-5">{user.email}</p>

        <div className="mb-6">
          <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${
            user.enabled
              ? 'bg-[#dcfce7] text-[#166534]'
              : 'bg-[#fee2e2] text-[#991b1b]'
          }`}>
            {user.enabled ? 'Email Verified' : 'Email Not Verified'}
          </span>
        </div>

        <div className="border-t border-[#e5e7eb] mb-6" />

        <button
          onClick={handleLogout}
          className="w-full py-3 rounded-xl text-sm font-semibold text-white uppercase
                     tracking-wide shadow-md hover:shadow-lg hover:-translate-y-0.5
                     transition-all duration-200"
          style={{ background: 'linear-gradient(135deg, #486881, #19135f)' }}
          onMouseEnter={e => e.currentTarget.style.background = 'linear-gradient(135deg, #19135f, #486881)'}
          onMouseLeave={e => e.currentTarget.style.background = 'linear-gradient(135deg, #486881, #19135f)'}
        >
          Sign Out
        </button>
      </div>
    </div>
  );
};

export default ProfilePage;