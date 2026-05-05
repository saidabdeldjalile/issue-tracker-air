import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import { AuthProvider } from "./context/AuthProvider";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./i18n";

// Polyfill for global object (required by sockjs-client)
(window as any).global = window;

// Enregistrement du Service Worker pour PWA
/* 
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker
      .register("/sw.js")
      .then((registration) => {
        console.log(
          "ServiceWorker registration successful with scope: ",
          registration.scope
        );
      })
      .catch((error) => {
        console.log("ServiceWorker registration failed: ", error);
      });
  });
}
*/

// Handle AbortError from browser extensions/auto-play requests
// This error occurs when play() is interrupted by pause() - commonly from browser extensions
window.addEventListener("unhandledrejection", (event) => {
  if (
    event.reason?.name === "AbortError" ||
    event.reason?.message?.includes("play() request was interrupted")
  ) {
    event.preventDefault();
    console.warn("Ignored AbortError from auto-play request");
  }
});

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter future={{
      v7_startTransition: true,
      v7_relativeSplatPath: true,
    }}>
      <AuthProvider>
        <Routes>
          <Route path="/*" element={<App />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);

