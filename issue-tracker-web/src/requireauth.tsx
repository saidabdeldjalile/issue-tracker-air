import { useEffect, useState } from "react";
import { useLocation, Navigate, Outlet } from "react-router-dom";
import useAuth from "./hooks/useAuth";

type StoredUser = Partial<{
  email: string;
  role: string | null;
  departmentId: number | null;
  firstName: string;
  lastName: string;
}>;

const RequireAuth = () => {
  const { auth, setAuth } = useAuth();
  const location = useLocation();
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token && !auth?.token) {
      const userRaw = localStorage.getItem("user");
      let user: StoredUser = {};
      if (userRaw) {
        try {
          user = JSON.parse(userRaw) as StoredUser;
        } catch {
          user = {};
        }
      }
      setAuth((prev: Record<string, unknown>) => ({ ...prev, token, ...user }));
    }
    setIsReady(true);
  }, []);

  if (!isReady) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  }

  const token = auth?.token || localStorage.getItem("token");
  
  return token ? <Outlet /> : <Navigate to="/login" state={{ from: location }} replace />;
};
export default RequireAuth;
