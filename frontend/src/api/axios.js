import axios from 'axios';
import router from '../router/index.js';

const api = axios.create({
  baseURL: 'http://localhost:3001',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        router.push('/login');
      } else if (status === 403) {
        console.error('Access forbidden:', data.message || 'You do not have permission to access this resource');
      } else if (status === 400) {
        return Promise.reject(error);
      }
    } else if (error.request) {
      console.error('Network error: Please check your connection');
    }
    return Promise.reject(error);
  }
);

export default api;