const config = {
  // Backend REST API base (Spring controllers live under `/api/v1/**` except auth)
  apiUrl: import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/v1` : 'http://localhost:6969/api/v1',
  authUrl: import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}/api/auth` : 'http://localhost:6969/api/auth',
  staticUrl: import.meta.env.VITE_API_URL ? `${import.meta.env.VITE_API_URL}` : 'http://localhost:6969',
  aiServiceUrl: import.meta.env.VITE_AI_SERVICE_URL || 'http://localhost:5001',
};

export default config;
