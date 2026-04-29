      import { useEffect, useState, useCallback, useRef } from 'react';
import api from '../api/axios';
import config from '../config';
import { Notification, NotificationResponse } from '../types/notification';
import { toast } from 'react-toastify';

export const useNotification = (email: string | undefined) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isConnected, setIsConnected] = useState(false);
  const [lastError, setLastError] = useState<string | null>(null);
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const reconnectAttemptsRef = useRef(0);
  const MAX_RECONNECT_ATTEMPTS = 3;

  const fetchNotifications = useCallback(async () => {
    if (!email) return;
    
    try {
      const response = await api.get<Notification[]>(`/notifications?email=${encodeURIComponent(email)}`);
      console.log('[Notification] Fetch response:', response.data);
      
      const data = (response.data as any)?.content || response.data;
      
      if (data && Array.isArray(data)) {
        setNotifications(data);
        const unread = data.filter(n => !n.isRead).length;
        setUnreadCount(unread);
      } else {
        console.warn('[Notification] Expected array of notifications, got:', typeof response.data, response.data);
        setNotifications([]);
        setUnreadCount(0);
      }
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  }, [email]);

  const fetchUnreadCount = useCallback(async () => {
    if (!email) return;
    
    try {
      const response = await api.get<NotificationResponse>(`/notifications/unread-count?email=${encodeURIComponent(email)}`);
      if (response.data && typeof response.data.count === 'number') {
        setUnreadCount(response.data.count);
      } else {
        console.warn('[Notification] Unexpected unread count response:', response.data);
      }
    } catch (error: any) {
      // Handle 401 errors - token might be expired or missing
      if (error.response?.status === 401) {
        console.warn('Unauthorized when fetching unread count - token may be expired');
        // Don't show error in console for 401 as it's handled by auth interceptor
        setUnreadCount(0);
      } else if (error.response?.status !== 404) {
        console.error('Error fetching unread count:', error);
      }
    }
  }, [email]);

  const connectToSSE = useCallback(async () => {
    if (!email || eventSourceRef.current) {
      console.log('[Notification] Skipping SSE connection - no email or already connected');
      return;
    }

    console.log('[Notification] Starting SSE connection for email:', email);

    // Clear any pending reconnect timeout
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }

    const apiUrl = config.apiUrl || 'http://localhost:6969/api/v1';
    console.log('[Notification] Using API URL:', apiUrl);

    // Skip health check for now - SSE connection will fail if server is down
    console.log('[Notification] Skipping health check, proceeding to user check');
    
    // Then check if user exists before trying to connect to SSE
    const checkUserExists = async () => {
      try {
        console.log('[Notification] Checking if user exists:', email);
        const response = await api.get(`/notifications?email=${encodeURIComponent(email)}`);
        console.log('[Notification] User check response status:', response.status);
        const exists = response.status === 200;
        return exists;
      } catch (error: any) {
        console.warn('[Notification] User check failed:', error.response?.status, error.message);
        if (error.response?.status === 404) {
          setLastError(`User not found: ${email}`);
          return false;
        }
        return false;
      }
    };

    const exists = await checkUserExists();
    if (!exists) {
      console.log('[Notification] User does not exist, skipping SSE connection');
      setIsConnected(false);
      return;
    }

    const sseUrl = `${apiUrl}/notifications/stream?email=${encodeURIComponent(email)}`;
    console.log('[Notification] Connecting to SSE:', sseUrl);
    
    // Note: Native EventSource doesn't support withCredentials option in constructor
    // We rely on CORS configuration on the server side
    const eventSource = new EventSource(sseUrl);

    eventSource.onopen = () => {
      console.log('[Notification] SSE connection opened successfully');
      setIsConnected(true);
      setLastError(null);
      reconnectAttemptsRef.current = 0;
    };

    eventSource.addEventListener('notification', (event) => {
      try {
        const notification: Notification = JSON.parse(event.data);
        console.log('[Notification] Received notification:', notification);
        
        setNotifications(prev => [notification, ...prev]);
        setUnreadCount(prev => prev + 1);
        
        const toastType = notification.type.includes('DELETED') ? 'error' : 'info';
        toast[toastType](notification.message, {
          position: 'top-right',
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
        });
      } catch (error) {
        console.error('[Notification] Error parsing notification:', error);
      }
    });

    eventSource.addEventListener('connected', () => {
      console.log('[Notification] Connected event received');
    });

    eventSource.addEventListener('error', (event: MessageEvent) => {
      try {
        const errorData = JSON.parse(event.data || '{}');
        console.error('[Notification] SSE error event:', errorData);
        if (errorData.message) {
          setLastError(errorData.message);
          toast.error(errorData.message, {
            position: 'top-right',
            autoClose: 5000,
          });
        }
      } catch (e) {
        console.error('[Notification] Error parsing SSE error data:', e);
      }
    });

    eventSource.onerror = (error) => {
      console.error('[Notification] SSE onerror triggered:', error);
      setIsConnected(false);
      
      eventSource.close();
      eventSourceRef.current = null;
      
      const errorMessage = `SSE connection failed for ${email} (attempt ${reconnectAttemptsRef.current + 1}/${MAX_RECONNECT_ATTEMPTS})`;
      console.error('[Notification]', errorMessage);
      setLastError(errorMessage);
      
      if (reconnectAttemptsRef.current < MAX_RECONNECT_ATTEMPTS) {
        reconnectAttemptsRef.current += 1;
        const backoffDelay = Math.min(30000, 1000 * Math.pow(2, reconnectAttemptsRef.current)); // Exponential backoff up to 30s
        console.log('[Notification] Scheduling reconnect in', backoffDelay, 'ms');
        
        reconnectTimeoutRef.current = setTimeout(() => {
          console.log('[Notification] Attempting reconnect...');
          if (email) {
            connectToSSE();
          }
        }, backoffDelay);
      } else {
        console.error('[Notification] Max reconnection attempts reached. Showing error toast.');
        toast.error('Notification service unavailable. Please check server status and refresh.', {
          position: 'top-right',
          autoClose: 10000,
        });
      }
    };

    eventSourceRef.current = eventSource;
    console.log('[Notification] EventSource created and stored in ref');
  }, [email]);

  const disconnectFromSSE = useCallback(() => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }
    
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
      setIsConnected(false);
    }
  }, []);

  const markAsRead = useCallback(async (notificationId: number) => {
    try {
      await api.post(`/notifications/${notificationId}/read`);
      setNotifications(prev => 
        prev.map(n => n.id === notificationId ? { ...n, isRead: true } : n)
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  }, []);

  const markAllAsRead = useCallback(async () => {
    if (!email) return;
    
    try {
      await api.post(`/notifications/read-all?email=${encodeURIComponent(email)}`);
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  }, [email]);

  useEffect(() => {
    if (email) {
      fetchNotifications();
      fetchUnreadCount();
      connectToSSE();
    }

    return () => {
      disconnectFromSSE();
    };
  }, [email, fetchNotifications, fetchUnreadCount, connectToSSE, disconnectFromSSE]);

  return {
    notifications,
    unreadCount,
    isConnected,
    lastError,
    fetchNotifications,
    fetchUnreadCount,
    markAsRead,
    markAllAsRead,
    connectToSSE,
    disconnectFromSSE
  };
};

export default useNotification;
