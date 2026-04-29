import {  Route, Routes } from "react-router-dom";

import Layout from "./layout";

import Loginform from "./loginform";
import ProjectList from "./projectlist";
import Registrationform from "./registrationform";
import Testing from "./test";
import { Ticket } from "./ticket";
import RequireAuth from "./requireauth";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Themes from "./components/themes";
import Home from "./Home";

export default function App() {
  // const navigate = useNavigate();

  // useEffect(() => {
  //   navigate("/projects");
  // }, []);
  return (
    <>
      <ToastContainer
        position="bottom-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="dark"

        // transition: Bounce,
      />
      <Routes>
        <Route path="/" element={<Layout />}>
          {/* <Route index element={<Navigate to="/projects" replace />} /> */}
          <Route index element={<Home/>} />
          <Route path="/projects" element={<ProjectList />} />
          <Route path="/register" element={<Registrationform />} />
          <Route path="/login" element={<Loginform />} />
          <Route element={<RequireAuth />}>
            <Route path="/projects/:pid/tickets" element={<Testing />} />
            <Route path="/projects/:pid/tickets/:id" element={<Ticket />} />
            <Route path="/tickets" element={<Testing />} />
            <Route path="/tickets/:id" element={<Ticket />} />
            {/* <Route path="/create" element={<CreateTicketBody />} /> */}
          </Route>
          <Route path="/themes" element={<Themes />} />
          <Route path="*" element={<h1>Not Found</h1>} />
        </Route>
      </Routes>
    </>
  );
}
