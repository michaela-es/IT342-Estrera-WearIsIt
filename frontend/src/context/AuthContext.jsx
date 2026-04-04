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
    const token = localStorage.getItem('accessToken');
    const savedUser = localStorage.getItem('user');

    checkOAuthCallback();

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

const handleApiError = (err, fallback = 'An error occurred') => {
  const message =
    err?.response?.data?.error?.message ||
    err?.response?.data?.message ||      
    fallback;

  setError(message); 
  return { success: false, error: message };
};

  const login = async (usernameOrEmail, password) => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.post('/auth/login', { usernameOrEmail, password });
      const { accessToken, user: userData } = response;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);

      return { success: true, data: userData };
    } catch (err) {
      return handleApiError(err, 'Login failed');
    } finally {
      setLoading(false);
    }
  };
const handleOAuthLogin = async (idToken) => {
  setLoading(true);
  setError(null);
  try {
    const response = await api.post('/auth/google', { idToken });
    const { accessToken, user: userData } = response;

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
const checkOAuthCallback = () => {
  const hash = window.location.hash.substring(1);
  const params = new URLSearchParams(hash);
  const accessToken = params.get('accessToken');
  
  if (accessToken) {
    window.location.hash = '';
    
    api.get('/user/me', {
      headers: { 'Authorization': `Bearer ${accessToken}` }
    }).then(response => {
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('user', JSON.stringify(response));
      setUser(response);
      window.location.href = '/profile';
    }).catch(() => {
      localStorage.removeItem('accessToken');
      window.location.href = '/login?error=oauth_failed';
    });
  }
};
  const register = async ({ name, email, password }) => {
    setLoading(true);
    setError(null);
    try {
      await api.post('/auth/register', { username: name, email, password });
      return { success: true };
    } catch (err) {
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
      console.error(err);
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