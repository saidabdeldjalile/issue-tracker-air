import axios, { AxiosHeaders } from "axios";
import config from "../config";

const api = axios.create({
  baseURL: config.apiUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to add Authorization header
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      // Normalize headers to AxiosHeaders to safely call .set across axios config variants
      const headers = AxiosHeaders.from(config.headers);
      headers.set("Authorization", `Bearer ${token}`);
      config.headers = headers;
      console.log("Adding auth header, token starts with:", token.substring(0, 30));
    } else {
      console.log("No token found in localStorage for request to:", config.url);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle 401 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired/invalid - clear auth only if we were previously logged in
      const hadToken = !!localStorage.getItem("token");
      if (hadToken && window.location.pathname !== "/login") {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

// Separate axios instance for AI Chatbot Service (port 5001)
const aiService = axios.create({
  baseURL: config.aiServiceUrl || 'http://localhost:5001',
  headers: {
    "Content-Type": "application/json",
  },
});

// Add auth token to AI service requests as well
aiService.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      const headers = AxiosHeaders.from(config.headers);
      headers.set("Authorization", `Bearer ${token}`);
      config.headers = headers;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export { aiService };
export default api;
