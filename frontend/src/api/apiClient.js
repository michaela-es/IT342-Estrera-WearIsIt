import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
  if (token && !config.url.startsWith('/auth')) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => {
    if (response.data && response.data.data) {
      return response.data.data;
    }
    return response.data;
  },
  (error) => {
    if (error.response?.status === 403) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
    }

    return Promise.reject(error);
  }
);

export default api;