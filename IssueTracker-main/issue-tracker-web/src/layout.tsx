import { Outlet } from "react-router-dom";
import NavBar from "./components/navbar";

function Layout() {
  return (
    <main>
      <NavBar />
      <Outlet />
    </main>
  );
}
export default Layout;
