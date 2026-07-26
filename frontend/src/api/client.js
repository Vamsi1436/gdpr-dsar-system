import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const client = axios.create({
baseURL: API_BASE_URL,
});

client.interceptors.request.use((config) => {
const token = localStorage.getItem('dsar_token');
if (token) {
config.headers.Authorization = `Bearer ${token}`;
}
return config;
});

client.interceptors.response.use(
(response) => response,
(error) => {
if (error.response && error.response.status === 401) {
localStorage.removeItem('dsar_token');
localStorage.removeItem('dsar_user');
window.location.href = '/login';
}
return Promise.reject(error);
}
);

export default client;
