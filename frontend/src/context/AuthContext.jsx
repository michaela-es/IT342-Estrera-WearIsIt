import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/apiClient';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [initializing, setInitializing] = useState(true);

  useEffect(() => {
    // Clean up any invalid tokens on startup
    const token = localStorage.getItem('accessToken');
    const savedUser = localStorage.getItem('user');
    
    // Remove invalid tokens
    if (token === 'undefined' || token === 'null' || token === '') {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      setUser(null);
      setInitializing(false);
      return;
    }
    
    // Only check OAuth callback if we're on the OAuth callback page
    if (window.location.pathname === '/oauth-callback') {
      checkOAuthCallback();
    }
    
    if (token && savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('user');
      }
    }
    setInitializing(false);
  }, []);

  useEffect(() => {
    const handleStorageChange = (e) => {
      if (e.key === 'user') setUser(e.newValue ? JSON.parse(e.newValue) : null);
      if (e.key === 'accessToken' && !e.newValue) setUser(null);
    };
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  // FIXED: Better error handling that captures the actual error message
  const handleApiError = (err, fallback = 'An error occurred') => {
    console.error('API Error:', err);
    
    // Try to get the error message from various possible locations
    let message = fallback;
    
    if (err?.message) {
      message = err.message;
    } else if (err?.response?.data?.error?.message) {
      message = err.response.data.error.message;
    } else if (err?.response?.data?.message) {
      message = err.response.data.message;
    } else if (err?.error?.message) {
      message = err.error.message;
    } else if (typeof err === 'string') {
      message = err;
    }
    
    setError(message);
    return { success: false, error: message };
  };

  // FIXED: Login function with better error handling
  const login = async (usernameOrEmail, password) => {
    setLoading(true);
    setError(null);
    try {
      console.log('Attempting login for:', usernameOrEmail);
      
      const response = await api.post('/auth/login', { 
        usernameOrEmail, 
        password 
      });
      
      let accessToken, userData;
      
      if (response && response.accessToken) {
        accessToken = response.accessToken;
        userData = response.user;
      } else if (response && response.data) {
        accessToken = response.data.accessToken;
        userData = response.data.user;
      } else {
        throw new Error('Invalid response format from server');
      }
      
      if (!accessToken) {
        throw new Error('No access token received');
      }
      
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      
      return { success: true, data: userData };
    } catch (err) {
      console.error('Login error details:', err);
      return handleApiError(err, 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };
  
  const handleOAuthLogin = async (idToken) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.post('/auth/google', { idToken });
      
      let accessToken, userData;
      if (response && response.accessToken) {
        accessToken = response.accessToken;
        userData = response.user;
      } else if (response && response.data) {
        accessToken = response.data.accessToken;
        userData = response.data.user;
      } else {
        throw new Error('Invalid response format');
      }
      
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      
      return { success: true, data: userData };
    } catch (err) {
      return handleApiError(err, 'Google login failed');
    } finally {
      setLoading(false);
    }
  };
  
  // FIXED: Only run on OAuth callback page
  const checkOAuthCallback = () => {
    const hash = window.location.hash.substring(1);
    const params = new URLSearchParams(hash);
    const accessToken = params.get('accessToken');
    
    if (accessToken && accessToken !== 'undefined') {
      window.location.hash = '';
      
      api.get('/user/me', {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      }).then(response => {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('user', JSON.stringify(response));
        setUser(response);
        window.location.href = '/profile';
      }).catch((err) => {
        console.error('OAuth callback failed:', err);
        localStorage.removeItem('accessToken');
        window.location.href = '/login?error=oauth_failed';
      });
    }
  };
  
  const register = async ({ name, email, password }) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.post('/auth/register', { 
        username: name, 
        email, 
        password 
      });
      
      console.log('Registration response:', response);
      return { success: true };
    } catch (err) {
      console.error('Registration error:', err);
      return handleApiError(err, 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    setLoading(true);
    try {
      await api.post('/auth/logout');
    } catch (err) {
      console.error('Logout error:', err);
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      setUser(null);
      setError(null);
      setLoading(false);
    }
  };
  
  const updateUser = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const clearError = () => setError(null);

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        error,
        initializing,
        isAuthenticated: !!user,
        login,
        register,
        logout,
        updateUser,
        clearError,
        handleOAuthLogin  
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};