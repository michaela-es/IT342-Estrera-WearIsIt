import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const isPublicEndpoint = config.url.includes('/auth/login') || 
                             config.url.includes('/auth/register') ||
                             config.url.includes('/auth/google');
    
    if (!isPublicEndpoint) {
      const token = localStorage.getItem('accessToken');
      if (token && token !== 'undefined' && token !== 'null') {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => {
    if (response.data && response.data.success === false) {
      return Promise.reject(response.data);
    }
        if (response.data && response.data.data) {
      return response.data.data;
    }
    
    return response.data;
  },
  (error) => {
    console.error('API Error:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      data: error.response?.data
    });
    
    let errorMessage = 'Network error or server not reachable';
    
    if (error.response?.data) {
      errorMessage = error.response.data.error?.message || 
                    error.response.data.message ||
                    error.response.data.errorMessage ||
                    `Server error: ${error.response.status}`;
    } else if (error.request) {
      errorMessage = 'No response from server. Please check if backend is running.';
    }
    
    return Promise.reject({
      success: false,
      message: errorMessage,
      originalError: error
    });
  }
);

export default api;