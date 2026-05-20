import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
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
    if (response.data && response.data.data !== undefined) {
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
    let errorCode = null;
    
    if (error.response?.data) {
      errorMessage = error.response.data.error?.message || 
                    error.response.data.message ||
                    `Server error: ${error.response.status}`;
      errorCode = error.response.data.code;
    } else if (error.request) {
      errorMessage = 'No response from server. Please check your connection.';
    }
    
    return Promise.reject({
      success: false,
      message: errorMessage,
      code: errorCode,
      status: error.response?.status,
      originalError: error
    });
  }
);

class ApiClient {
  constructor() {
    this.api = api;
  }

  get(endpoint, params = {}) {
    return this.api.get(endpoint, { params });
  }

  post(endpoint, body) {
    return this.api.post(endpoint, body);
  }

  put(endpoint, body) {
    return this.api.put(endpoint, body);
  }

  delete(endpoint) {
    return this.api.delete(endpoint);
  }
}

export const apiClient = new ApiClient();
export default apiClient;