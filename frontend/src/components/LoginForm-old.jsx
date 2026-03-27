import React from 'react';

const LoginForm = ({ formData, handleChange, loading }) => {
  return (
    <>
      <div className="mb-5 w-full">
        <label
          htmlFor="emailOrUsername"
          className="block mb-1.5 text-[#4b5563] font-medium text-sm"
        >
          Username or Email
        </label>
        <input
          type="text"
          id="emailOrUsername"
          name="emailOrUsername"
          value={formData.emailOrUsername}
          onChange={handleChange}
          placeholder="Enter your username or email"
          disabled={loading}
          required
          className="w-full px-4 py-3 border-2 border-[#d1d5db] rounded-xl text-sm
                     bg-white shadow-sm transition-colors duration-300
                     focus:outline-none focus:border-[#4F46E5]
                     disabled:bg-gray-100 disabled:cursor-not-allowed
                     placeholder:text-[#9ca3af]"
        />
      </div>

      <div className="mb-5 w-full">
        <label
          htmlFor="password"
          className="block mb-1.5 text-[#4b5563] font-medium text-sm"
        >
          Password
        </label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          placeholder="Enter your password"
          disabled={loading}
          required
          minLength="6"
          className="w-full px-4 py-3 border-2 border-[#d1d5db] rounded-xl text-sm
                     bg-white shadow-sm transition-colors duration-300
                     focus:outline-none focus:border-[#4F46E5]
                     disabled:bg-gray-100 disabled:cursor-not-allowed
                     placeholder:text-[#9ca3af]"
        />
      </div>
    </>
  );
};

export default LoginForm;