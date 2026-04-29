import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { AuthProvider } from "./context/AuthProvider.jsx";
import { BrowserRouter, Routes, Route } from "react-router-dom";

// const router = createBrowserRouter([
//   {
//     path: "/",
//     element: <App />,
//     errorElement: <ErrorPage />,
//   },
//   // {
//   //   path: "test",
//   //   element: <TicketList />,
//   //   errorElement: <ErrorPage />,
//   // },
//   {
//     path: "tickets/:id",
//     element: <Ticket />,
//   },
//   {
//     path: "projects/:pid/tickets",
//     element: <Testing />,
//   },
//   {
//     path: "projects/:pid/tickets/:id",
//     element: <Ticket />,
//   },
//   {
//     path: "create",
//     element: <CreateTicketBody />,
//   },
//   {
//     path: "tickets",
//     element: <Testing />,
//   },
//   {
//     path: "projects",
//     element: <ProjectList />,
//   },
//   {
//     path: "register",
//     element: <Registrationform />,
//   },
//   {
//     path: "login",
//     element: <Loginform />,
//   },
// ]);

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/*" element={<App />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
);
