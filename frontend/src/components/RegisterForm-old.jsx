import React from 'react';

const inputClass = `
  w-full px-4 py-3 border-2 border-[#d1d5db] rounded-xl text-sm
  bg-white shadow-sm transition-colors duration-300
  focus:outline-none focus:border-[#4F46E5]
  disabled:bg-gray-100 disabled:cursor-not-allowed
  placeholder:text-[#9ca3af]
`;

const labelClass = "block mb-1.5 text-[#4b5563] font-medium text-sm";
const groupClass = "mb-5 w-full";

const RegisterForm = ({ formData, handleChange, loading }) => {
  return (
    <>
      <div className={groupClass}>
        <label htmlFor="name" className={labelClass}>Username</label>
        <input
          type="text"
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="Enter your username"
          disabled={loading}
          required
          className={inputClass}
        />
      </div>

      <div className={groupClass}>
        <label htmlFor="email" className={labelClass}>Email Address</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Enter your email"
          disabled={loading}
          required
          className={inputClass}
        />
      </div>

      <div className={groupClass}>
        <label htmlFor="password" className={labelClass}>Password</label>
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
          className={inputClass}
        />
      </div>

      <div className={groupClass}>
        <label htmlFor="confirmPassword" className={labelClass}>Confirm Password</label>
        <input
          type="password"
          id="confirmPassword"
          name="confirmPassword"
          value={formData.confirmPassword}
          onChange={handleChange}
          placeholder="Confirm your password"
          disabled={loading}
          required
          minLength="6"
          className={inputClass}
        />
      </div>
    </>
  );
};

export default RegisterForm;