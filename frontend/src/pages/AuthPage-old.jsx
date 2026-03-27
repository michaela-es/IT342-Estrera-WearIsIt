import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
import MessageBox from '../components/MessageBox';

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    emailOrUsername: '',
    password: '',
    confirmPassword: ''
  });
  const { login, register, loading, error, clearError } = useAuth();
  const [success, setSuccess] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) clearError();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isLogin) {
        const data = await login(formData.emailOrUsername, formData.password);
        if (data) {
          setSuccess("Welcome back!");
          navigate('/profile');
        }
      } else {
        if (formData.password !== formData.confirmPassword) return;
        const data = await register(formData);
        if (data.success) {
          setSuccess("Account created successfully! You can now sign in.");
          setFormData({ name: '', email: '', emailOrUsername: '', password: '', confirmPassword: '' });
        }
      }
    } catch (err) {}
  };

  const messageText = error || success;
  const messageType = error ? 'error' : success ? 'success' : null;

  return (
    <div className="min-h-screen flex items-center justify-center w-full bg-[#f3f4f6] px-4">
      <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-xl">

        <div className="text-center mb-6">
          <h1 className="text-3xl font-semibold text-[#111827]">
            {isLogin ? 'Sign In' : 'Create Account'}
          </h1>
        </div>

        <MessageBox
          type={messageType}
          text={messageText}
          onClose={() => {
            if (error) clearError();
            if (success) setSuccess(null);
          }}
        />

        <form onSubmit={handleSubmit} className="mb-4 w-full">
          {isLogin ? (
            <LoginForm formData={formData} handleChange={handleChange} loading={loading} />
          ) : (
            <RegisterForm formData={formData} handleChange={handleChange} loading={loading} />
          )}

          <button
            type="submit"
            style={{ background: 'linear-gradient(135deg, #486881, #19135f)' }}
            className="w-full py-3.5 mt-2 text-white rounded-xl text-sm font-semibold uppercase
                       tracking-wide shadow-md hover:shadow-lg hover:-translate-y-0.5
                       disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none
                       transition-all duration-200"
            disabled={loading}
            onMouseEnter={e => e.currentTarget.style.background = 'linear-gradient(135deg, #19135f, #486881)'}
            onMouseLeave={e => e.currentTarget.style.background = 'linear-gradient(135deg, #486881, #19135f)'}
          >
            {loading ? 'Please wait...' : isLogin ? 'Sign In' : 'Sign Up'}
          </button>
        </form>

        <div className="text-center border-t border-[#e5e7eb] pt-4 mt-4">
          <p className="text-[#4b5563] text-sm">
            {isLogin ? "Don't have an account?" : "Already have an account?"}
            <button
              type="button"
              className="ml-1.5 text-[#4F46E5] font-semibold bg-transparent border-none
                         cursor-pointer hover:underline hover:text-[#4338ca]
                         disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              onClick={() => { setIsLogin(!isLogin); clearError(); }}
              disabled={loading}
            >
              {isLogin ? 'Sign up' : 'Sign in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;