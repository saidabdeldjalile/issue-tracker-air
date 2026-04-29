import { Outlet } from "react-router-dom";
import NavBar from "./components/navbar";

function Layout() {
  return (
    <main className="min-h-screen pt-20">
      <NavBar />
      <div className="page-shell">
        <Outlet />
      </div>
    </main>
  );
}
export default Layout;
