import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import useAuth from "./hooks/useAuth";
import useNotification from "./hooks/useNotification";
import { Notification, NotificationType } from "./types/notification";

function getNotificationIcon(type: NotificationType) {
  switch (type) {
    case NotificationType.PROJECT_CREATED:
      return "📁";
    case NotificationType.PROJECT_DELETED:
      return "🗑️";
    case NotificationType.TICKET_CREATED:
      return "🎫";
    case NotificationType.TICKET_STATUS_CHANGED:
      return "🔄";
    case NotificationType.TICKET_ASSIGNED:
      return "👤";
    case NotificationType.TICKET_DELETED:
      return "❌";
    default:
      return "🔔";
  }
}

function getNotificationColor(type: NotificationType) {
  switch (type) {
    case NotificationType.PROJECT_CREATED:
      return "bg-red-500/10 text-red-600";
    case NotificationType.TICKET_CREATED:
      return "bg-emerald-500/10 text-emerald-600";
    case NotificationType.TICKET_STATUS_CHANGED:
      return "bg-amber-500/10 text-amber-600";
    case NotificationType.TICKET_ASSIGNED:
      return "bg-red-800/10 text-red-800";
    case NotificationType.TICKET_DELETED:
    case NotificationType.PROJECT_DELETED:
      return "bg-red-500/10 text-red-600";
    default:
      return "bg-slate-500/10 text-slate-600";
  }
}

function formatTimeAgo(dateString: string, t: (key: string, options?: any) => string) {
  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return t("notifications.time.justNow");
  if (diffMins < 60) return t("notifications.time.minutesAgo", { count: diffMins });
  if (diffHours < 24) return t("notifications.time.hoursAgo", { count: diffHours });
  if (diffDays < 7) return t("notifications.time.daysAgo", { count: diffDays });
  return date.toLocaleDateString();
}

export default function NotificationsPage() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { auth } = useAuth();
  const email = auth?.email;

  const {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead,
    fetchNotifications,
  } = useNotification(email);

  useEffect(() => {
    if (email) {
      void fetchNotifications();
    }
  }, [email, fetchNotifications]);

  const handleNotificationClick = (notification: Notification) => {
    if (!notification.isRead) {
      void markAsRead(notification.id);
    }
  };

  return (
    <div className="space-y-8">
      <section className="page-section overflow-hidden">
        <div className="border-b border-base-300/60 bg-gradient-to-r from-red-600/10 via-red-800/5 to-gray-500/5 p-6 md:p-8">
          <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
            <div className="space-y-3.5">
              <div className="inline-flex items-center gap-2 rounded-full border border-base-300/70 bg-base-100/80 px-4 py-2 text-xs font-bold uppercase tracking-[0.2em] text-base-content/60 shadow-sm">
                {t("notifications.alerts")}
              </div>
              <div>
                <h1 className="section-heading">{t("notifications.title")}</h1>
                <p className="mt-2.5 max-w-2xl text-sm leading-6 text-base-content/65">
                  {t("notifications.description")}
                </p>
              </div>
            </div>

            <div className="flex flex-wrap gap-3">
              {unreadCount > 0 && (
                <button onClick={markAllAsRead} className="btn btn-primary">
                  {t("notifications.markAllRead")}
                </button>
              )}
              <button onClick={() => navigate(-1)} className="btn btn-ghost">
                ← {t("common.back")}
              </button>
            </div>
          </div>
        </div>

        <div className="p-6 md:p-8">
          {unreadCount > 0 && (
            <div className="alert alert-info mb-6 rounded-2xl border border-info/20 shadow-sm">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-6 w-6 shrink-0 stroke-current"
                fill="none"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
              <span>
                {t("notifications.unread", {
                  count: unreadCount,
                  pluralize: unreadCount > 1 ? "s" : "",
                })}
              </span>
            </div>
          )}

          <div>
            {notifications.length === 0 ? (
                 <div className="flex min-h-[320px] flex-col items-center justify-center rounded-2xl border border-dashed border-base-300/70 bg-base-200/40 p-10 text-center">
                 <div className="flex h-20 w-20 items-center justify-center rounded-full bg-gradient-to-br from-red-500/10 to-gray-500/10 mb-4">
                   <svg xmlns="http://www.w3.org/2000/svg" className="h-10 w-10 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                     <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                   </svg>
                 </div>
                 <h2 className="mt-4 text-2xl font-bold">{t("notifications.empty")}</h2>
                 <p className="mt-2 max-w-md text-sm text-base-content/60">
                   {t("notifications.emptyDesc")}
                 </p>
               </div>
            ) : (
              <div className="space-y-3">
                {notifications.map((notification) => (
                  <div
                    key={notification.id}
                    onClick={() => handleNotificationClick(notification)}
                    className={`cursor-pointer rounded-2xl border p-5 shadow-sm transition-all hover:shadow-md ${
                      !notification.isRead
                        ? "border-primary/30 bg-primary/5 hover:bg-primary/10"
                        : "border-base-300/60 bg-base-100/80 hover:bg-base-200/50"
                    }`}
                  >
                    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                      <div className="flex items-start gap-4">
                        <div
                          className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl ${getNotificationColor(
                            notification.type
                          )}`}
                        >
                          {getNotificationIcon(notification.type)}
                        </div>
                        <div className="flex-1 min-w-0">
                          <h3 className="font-bold text-base-content">
                            {notification.title}
                            {!notification.isRead && (
                              <span className="ml-2 badge badge-primary badge-sm">
                                {t("notifications.new")}
                              </span>
                            )}
                          </h3>
                          <p className="mt-1 text-sm text-base-content/70">
                            {notification.message}
                          </p>
                          <div className="mt-2 flex flex-wrap items-center gap-2 text-xs text-base-content/50">
                            {notification.departmentName && (
                              <span className="rounded-full bg-base-200 px-3 py-1">
                                {notification.departmentName}
                              </span>
                            )}
                            <span>{formatTimeAgo(notification.createdAt, t)}</span>
                          </div>
                        </div>
                      </div>
                      <div className="flex shrink-0 items-center gap-2 self-start">
                        {!notification.isRead ? (
                          <span className="h-2.5 w-2.5 rounded-full bg-primary shadow-glow" />
                        ) : (
                          <span className="h-2.5 w-2.5 rounded-full bg-base-content/20" />
                        )}
                      </div>
                    </div>
                  </div>
                ))}

                <div className="flex items-center justify-center gap-3 rounded-2xl border border-base-300/60 bg-base-100/80 px-6 py-4 text-sm text-base-content/60 shadow-sm">
                  <span className="h-1.5 w-1.5 rounded-full bg-success" />
                  {t("notifications.upToDate")}
                </div>
              </div>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
