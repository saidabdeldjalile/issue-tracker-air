import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Notification, NotificationType } from '../types/notification';
import useNotification from '../hooks/useNotification';
import useAuth from '../hooks/useAuth';
import { useTranslation } from 'react-i18next';
import { 
  Bell, 
  CheckCheck, 
  FolderPlus, 
  Trash2, 
  Ticket as TicketIcon, 
  RefreshCcw, 
  User, 
  XCircle,
  Inbox
} from 'lucide-react';

export default function NotificationBell() {
  const { t } = useTranslation();
  const { auth } = useAuth();
  const email = auth?.email;
  const {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead
  } = useNotification(email);
  
  const [isOpen, setIsOpen] = useState(false);

  const getNotificationIcon = (type: NotificationType) => {
    switch (type) {
      case NotificationType.PROJECT_CREATED:
        return <FolderPlus className="w-4 h-4 text-blue-500" />;
      case NotificationType.PROJECT_DELETED:
        return <Trash2 className="w-4 h-4 text-red-500" />;
      case NotificationType.TICKET_CREATED:
        return <TicketIcon className="w-4 h-4 text-green-500" />;
      case NotificationType.TICKET_STATUS_CHANGED:
        return <RefreshCcw className="w-4 h-4 text-amber-500" />;
      case NotificationType.TICKET_ASSIGNED:
        return <User className="w-4 h-4 text-purple-500" />;
      case NotificationType.TICKET_DELETED:
        return <XCircle className="w-4 h-4 text-red-600" />;
      default:
        return <Bell className="w-4 h-4 text-gray-500" />;
    }
  };

  const formatTimeAgo = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return t('time.today');
    if (diffMins < 60) return t('notification.time', { time: `${diffMins} min` });
    if (diffHours < 24) return t('notification.time', { time: `${diffHours} h` });
    if (diffDays < 7) return t('notification.time', { time: `${diffDays} j` });
    return date.toLocaleDateString();
  };

  const handleNotificationClick = (notification: Notification) => {
    if (!notification.isRead) {
      markAsRead(notification.id);
    }
    setIsOpen(false);
  };

  return (
    <div className={`dropdown dropdown-end z-[60] ${isOpen ? 'dropdown-open' : ''}`}>
         <button
          tabIndex={0}
          className="relative h-10 w-10 rounded-xl bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-300 flex items-center justify-center group"
          onClick={() => setIsOpen(!isOpen)}
        >
          <div className="absolute inset-0 rounded-xl bg-gradient-to-r from-red-500 to-red-600 opacity-0 group-hover:opacity-20 transition-opacity duration-300"></div>
          <Bell className="w-5 h-5 text-gray-700 dark:text-gray-300 group-hover:scale-110 transition-transform duration-300" />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-red-600 text-[10px] font-bold text-white shadow-lg border-2 border-white dark:border-gray-900 animate-pulse">
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div
          tabIndex={0}
          className="dropdown-content mt-2 z-[60] shadow-2xl bg-white dark:bg-gray-900 rounded-2xl w-80 max-h-96 overflow-hidden border border-gray-200 dark:border-gray-700 animate-fadeIn"
        >
          <div className="flex justify-between items-center p-4 border-b border-gray-100 dark:border-gray-800 bg-red-50/50 dark:bg-red-900/10">
            <h3 className="font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <Bell className="w-4 h-4 text-red-600" />
              {t('notifications.title')}
            </h3>
            {unreadCount > 0 && (
              <button
                onClick={markAllAsRead}
                className="text-xs text-red-600 hover:text-red-700 font-bold transition-colors duration-200 flex items-center gap-1 bg-red-100 dark:bg-red-500/20 px-2 py-1 rounded-lg"
              >
                <CheckCheck className="w-3 h-3" />
                {t('notifications.markAllAsRead')}
              </button>
            )}
          </div>

          <div className="overflow-y-auto max-h-72">
            {notifications.length === 0 ? (
              <div className="p-8 text-center">
                <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 rounded-2xl flex items-center justify-center mx-auto mb-4 text-gray-400">
                  <Inbox className="w-8 h-8" />
                </div>
                <p className="text-sm font-bold text-gray-900 dark:text-white">{t('notifications.noNotifications')}</p>
                <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">{t('notification.noNotifications')}</p>
              </div>
            ) : (
              notifications.slice(0, 20).map((notification) => (
                <div
                  key={notification.id}
                  onClick={() => handleNotificationClick(notification)}
                  className={`p-3 border-b border-gray-100 dark:border-gray-800 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-all duration-200 ${
                    !notification.isRead ? 'bg-red-50/30 dark:bg-red-500/5 border-l-2 border-l-red-600' : ''
                  }`}
                >
                  <div className="flex gap-3">
                    <span className="flex-shrink-0 mt-1">
                      {getNotificationIcon(notification.type)}
                    </span>
                    <div className="flex-1 min-w-0">
                      <p className={`text-xs font-bold leading-tight ${
                        !notification.isRead ? 'text-gray-900 dark:text-white' : 'text-gray-500 dark:text-gray-400'
                      }`}>
                        {notification.title}
                      </p>
                      <p className="text-[11px] text-gray-500 dark:text-gray-400 mt-1 line-clamp-2">
                        {notification.message}
                      </p>
                      <p className="text-[10px] text-gray-400 dark:text-gray-500 mt-1 font-medium">
                        {formatTimeAgo(notification.createdAt)}
                      </p>
                    </div>
                    {!notification.isRead && (
                      <span className="w-2 h-2 bg-red-600 rounded-full flex-shrink-0 mt-1 shadow-sm"></span>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>

          {notifications.length > 0 && (
            <div className="p-3 border-t border-gray-100 dark:border-gray-800 text-center bg-gray-50/50 dark:bg-gray-900/50">
              <Link
                to="/notifications"
                className="text-xs font-bold text-red-600 hover:text-red-700 transition-colors duration-200 flex items-center justify-center gap-2"
                onClick={() => setIsOpen(false)}
              >
                {t('notification.title')}
                <span className="text-lg">→</span>
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
