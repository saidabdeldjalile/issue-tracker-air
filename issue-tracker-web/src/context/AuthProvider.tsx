import React, { createContext, useState, ReactNode, useEffect } from "react";
import axios from "../api/axios";

export interface AuthContextType {
  auth: any;
  setAuth: React.Dispatch<React.SetStateAction<any>>;
}

const AuthContext = createContext<AuthContextType>({
  auth: {},
  setAuth: () => {},
});

export interface AuthProviderProps {
  children: ReactNode;
}

// Helper function to get auth from localStorage
const getAuthFromStorage = () => {
  try {
    const token = localStorage.getItem("token");
    const user = localStorage.getItem("user");
    if (!token) return {};

    // Token is enough to keep the user "logged in"; user info can be hydrated later.
    if (user) {
      try {
        return { token, ...JSON.parse(user) };
      } catch (error) {
        console.error("Error parsing user from localStorage:", error);
        return { token };
      }
    }

    return { token };
  } catch (error) {
    console.error("Error reading auth from localStorage:", error);
  }
  return {};
};

// Function to fetch user info from API
const fetchUserInfo = async (email: string, token: string) => {
  try {
    const response = await axios.get(`/users/by-email/${encodeURIComponent(email)}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return {
      firstName: response.data.firstName,
      lastName: response.data.lastName
    };
  } catch (error) {
    console.error("Error fetching user info:", error);
    return null;
  }
};

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [auth, setAuth] = useState<any>(getAuthFromStorage());

  useEffect(() => {
    const handleStorageChange = () => {
      const newAuth = getAuthFromStorage();
      setAuth(newAuth);
    };
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  // Persist auth to localStorage whenever it changes
  useEffect(() => {
    try {
      if (auth?.token) {
        localStorage.setItem("token", auth.token);
        localStorage.setItem("user", JSON.stringify({ 
          email: auth.email, 
          role: auth.role,
          departmentId: auth.departmentId,
          firstName: auth.firstName,
          lastName: auth.lastName
        }));
      } else {
        // Avoid nuking storage during transient hydration states; explicit logout clears storage elsewhere.
        const existingToken = localStorage.getItem("token");
        const existingUser = localStorage.getItem("user");
        if (!existingToken && !existingUser) return;
      }
    } catch (error) {
      console.error("Error saving auth to localStorage:", error);
    }
  }, [auth]);

  // Fetch user info on initial load if we have token but no name
  useEffect(() => {
    const loadUserInfo = async () => {
      const storedUser = localStorage.getItem("user");
      if (storedUser && auth?.token) {
        try {
          const userData = JSON.parse(storedUser);
          if (userData.email && !userData.firstName) {
            const userInfo = await fetchUserInfo(userData.email, auth.token);
            if (userInfo) {
              setAuth((prev: any) => ({
                ...prev,
                firstName: userInfo.firstName,
                lastName: userInfo.lastName
              }));
            }
          }
        } catch (error) {
          console.error("Error loading user info:", error);
        }
      }
    };
    loadUserInfo();
  }, []);

  return (
    <AuthContext.Provider value={{ auth, setAuth }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
