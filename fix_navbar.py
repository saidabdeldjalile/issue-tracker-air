import os

content = r"""import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import NotificationBell from "./NotificationBell";
import LanguageSelector from "./LanguageSelector";
import ChatBot from "./ChatBot";

export default function NavBar() {
  const { auth, setAuth } = useAuth();
  const navigate = useNavigate();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isDarkTheme, setIsDarkTheme] = useState(false);
  const [isChatBotOpen, setIsChatBotOpen] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  const isAuthenticated = Boolean(auth?.token);

  useEffect(() => {
    const html = document.documentElement;
    const theme = html.getAttribute("data-theme") || "light";
    setIsDarkTheme(theme === "dark");
  }, []);

  const handleThemeToggle = () => {
    const html = document.documentElement;
    const newTheme = isDarkTheme ? "light" : "dark";
    html.setAttribute("data-theme", newTheme);
    setIsDarkTheme(!isDarkTheme);
  };

  const handleLogout = () => {
    setAuth({});
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setIsDropdownOpen(false);
    navigate("/login");
  };

  const navigationItems = useMemo(
    () => [
      { label: "Dashboard", to: "/dashboard", adminOnly: true },
      { label: "Projects", to: "/projects" },
      { label: "Tickets", to: "/my-tickets" },
      { label: "Knowledge", to: "/knowledge", adminOnly: true },
      { label: "Users", to: "/users", adminOnly: true },
    ],
    [],
  );

  const accessibleNavItems = navigationItems.filter(
    (item) => !item.adminOnly || auth?.role === "ADMIN"
  );

  return (
    <>
      {/* Mobile Drawer */}
      <div className={`drawer drawer-end z-[70] ${isDrawerOpen ? "drawer-open" : ""}`}>
        <input
          type="checkbox"
          className="drawer-toggle"
          checked={isDrawerOpen}
          onChange={() => setIsDrawerOpen(!isDrawerOpen)}
        />
        <div className="drawer-content">
          {/* Navbar */}
          <div className="navbar sticky top-0 z-40">
            <div className="navbar-start gap-4">
              {/* Hamburger button for mobile */}
              <button
                className="btn btn-ghost btn-circle lg:hidden"
                onClick={() => setIsDrawerOpen(true)}
                aria-label="Open menu"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              </button>

              <Link to="/" className="flex items-center gap-3 rounded-2xl px-2 py-1 transition hover:bg-base-200/70">
                <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-blue-600 to-violet-600 text-white shadow-lg shadow-blue-600/25">
                  <span className="text-sm font-black tracking-wide">AA</span>
                </div>
                <div className="hidden flex-col leading-tight sm:flex">
                  <span className="text-sm font-semibold text-base-content/70">Air Algérie</span>
                  <span className="text-base font-bold tracking-tight text-base-content">Issue Tracker</span>
                </div>
              </Link>
            </div>

            <div className="navbar-center hidden lg:flex">
              {isAuthenticated && (
                <div className="tabs tabs-boxed gap-1">
                  {accessibleNavItems.map((item) => (
                    <Link key={item.to} to={item.to} className="tab px-4">
                      {item.label}
                    </Link>
                  ))}
                </div>
              )}
            </div>

            <div className="navbar-end gap-2">
              <LanguageSelector />

              <label className="swap swap-rotate rounded-full border border-base-300 bg-base-100/80 p-2 shadow-sm transition hover:shadow-md">
                <input
                  type="checkbox"
                  className="theme-controller"
                  value="dark"
                  checked={isDarkTheme}
                  onChange={handleThemeToggle}
                />
                <svg
                  className="swap-on h-5 w-5 fill-current text-amber-400"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                >
                  <path d="M5.64,17l-.71.71a1,1,0,0,0,0,1.41,1,1,0,0,0,1.41,0l.71-.71A1,1,0,0,0,5.64,17ZM5,12a1,1,0,0,0-1-1H3a1,1,0,0,0,0,2H4A1,1,0,0,0,5,12Zm7-7a1,1,0,0,0,1-1V3a1,1,0,0,0-2,0V4A1,1,0,0,0,12,5ZM5.64,7.05a1,1,0,0,0,.7.29,1,1,0,0,0,.71-.29,1,1,0,0,0,0-1.41l-.71-.71A1,1,0,0,0,4.93,6.34Zm12,.29a1,1,0,0,0,.7-.29l.71-.71a1,1,0,1,0-1.41-1.41L17,5.64a1,1,0,0,0,0,1.41A1,1,0,0,0,17.66,7.34ZM21,11H20a1,1,0,0,0,0,2h1a1,1,0,0,0,0-2Zm-9,8a1,1,0,0,0-1,1v1a1,1,0,0,0,2,0V20A1,1,0,0,0,12,19ZM18.36,17A1,1,0,0,0,17,18.36l.71.71a1,1,0,0,0,1.41,0,1,1,0,0,0,0-1.41ZM12,6.5A5.5,5.5,0,1,0,17.5,12,5.51,5.51,0,0,0,12,6.5Zm0,9A3.5,3.5,0,1,1,15.5,12,3.5,3.5,0,0,1,12,15.5Z" />
                </svg>
                <svg
                  className="swap-off h-5 w-5 fill-current text-slate-600"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                >
                  <path d="M21.64,13a1,1,0,0,0-1.05-.14,8.05,8.05,0,0,1-3.37.73A8.15,8.15,0,0,1,9.08,5.49a8.59,8.59,0,0,1,.25-2A1,1,0,0,0,8,2.36,10.14,10.14,0,1,0,22,14.05,1,1,0,0,0,21.64,13Zm-9.5,6.69A8.14,8.14,0,0,1,7.08,5.22v.27A10.15,10.15,0,0,0,17.22,15.63a9.79,9.79,0,0,0,2.1-.22A8.11,8.11,0,0,1,12.14,19.73Z" />
                </svg>
              </label>

              {isAuthenticated && (
                <div className="flex items-center gap-2">
                  <NotificationBell />
                  <button
                    onClick={() => setIsChatBotOpen(true)}
                    className="btn btn-ghost btn-circle"
                    title="Assistant virtuel"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
                    </svg>
                  </button>

                  <div className={`dropdown dropdown-end z-[60] ${isDropdownOpen ? "dropdown-open" : ""}`}>
                    <button
                      tabIndex={0}
                      className="btn btn-ghost rounded-full border border-base-300 bg-base-100/70 px-3 shadow-sm hover:border-primary/30"
                      onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                    >
                      <div className="flex items-center gap-3">
                        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br from-slate-900 to-slate-600 text-xs font-bold text-white dark:from-slate-100 dark:to-slate-300 dark:text-slate-900">
                          {(auth?.firstName?.charAt(0) || "") + (auth?.lastName?.charAt(0) || "")}
                        </div>
                        <div className="hidden text-left md:block">
                          <div className="text-sm font-semibold leading-tight">
                            {auth?.firstName} {auth?.lastName}
                          </div>
                          <div className="text-xs text-base-content/60">{auth?.role}</div>
                      </div>
                    </button>

                    {isDropdownOpen && (
                      <ul tabIndex={0} className="dropdown-content menu mt-3 w-60 p-3 z-[60]">
                        <li className="pointer-events-none mb-2 rounded-xl bg-base-200/70 px-3 py-3">
                          <span className="text-sm font-semibold">
                            {auth?.firstName} {auth?.lastName}
                          </span>
                          <span className="text-xs text-base-content/60">{auth?.role}</span>
                        </li>
                        {auth?.role === "ADMIN" && (
                          <li>
                            <Link to="/services" onClick={() => setIsDropdownOpen(false)}>
                              Services
                            </Link>
                          </li>
                        )}
                        <li>
                          <Link to="/projects" onClick={() => setIsDropdownOpen(false)}>
                            Projects
                          </Link>
                        </li>
                        <li>
                          <Link to="/my-tickets" onClick={() => setIsDropdownOpen(false)}>
                            Tickets
                          </Link>
                        </li>
                        {auth?.role === "ADMIN" && (
                          <li>
                            <Link to="/users" onClick={() => setIsDropdownOpen(false)}>
                              Users
                            </Link>
                          </li>
                        )}
                        {auth?.role === "ADMIN" && (
                          <li>
                            <Link to="/knowledge" onClick={() => setIsDropdownOpen(false)}>
                              Knowledge
                            </Link>
                          </li>
                        )}
                        {auth?.role === "ADMIN" && (
                          <li>
                            <Link to="/dashboard" onClick={() => setIsDropdownOpen(false)}>
                              Dashboard
                            </Link>
                          </li>
                        )}
                        <li>
                          <Link to="/settings" onClick={() => setIsDropdownOpen(false)}>
                            Settings
                          </Link>
                        </li>
                        <li className="mt-2 border-t border-base-300 pt-2">
                          <button onClick={handleLogout} className="text-error">
                            Logout
                          </button>
                        </li>
                      </ul>
                    )}
                  </div>
              )}
            </div>
        </div>

        {/* Drawer Side - Mobile Navigation */}
        <div className="drawer-side z-[70]">
          <label
            htmlFor="drawer-toggle"
            aria-label="close sidebar"
            className="drawer-overlay"
            onClick={() => setIsDrawerOpen(false)}
          ></label>
          <div className="min-h-full w-80 bg-base-200 p-6 text-base-content">
            <div className="mb-6 flex items-center justify-between">
              <span className="text-lg font-bold">Menu</span>
              <button
                className="btn btn-ghost btn-circle btn-sm"
                onClick={() => setIsDrawerOpen(false)}
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            {isAuthenticated ? (
              <ul className="menu gap-2">
                {accessibleNavItems.map((item) => (
                  <li key={item.to}>
                    <Link
                      to={item.to}
                      className="rounded-xl text-base font-medium"
                      onClick={() => setIsDrawerOpen(false)}
                    >
                      {item.label}
                    </Link>
                  </li>
                ))}
                <li className="mt-4 border-t border-base-300 pt-4">
                  <Link
                    to="/settings"
                    className="rounded-xl text-base font-medium"
                    onClick={() => setIsDrawerOpen(false)}
                  >
                    Settings
                  </Link>
                </li>
                <li>
                  <button
                    onClick={() => {
                      setIsDrawerOpen(false);
                      handleLogout();
                    }}
                    className="rounded-xl text-base font-medium text-error"
                  >
                    Logout
                  </button>
                </li>
              </ul>
            ) : (
              <ul className="menu gap-2">
                <li>
                  <Link
                    to="/login"
                    className="rounded-xl text-base font-medium"
                    onClick={() => setIsDrawerOpen(false)}
                  >
                    Login
                  </Link>
                </li>
              </ul>
            )}
          </div>
