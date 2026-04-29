import { useEffect, useRef, useState, useCallback } from 'react';
import { Client, Message } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface UseWebSocketOptions {
  url?: string;
  onMessage?: (message: unknown) => void;
  onConnect?: () => void;
  onDisconnect?: () => void;
  reconnectInterval?: number;
}

export function useWebSocket(options: UseWebSocketOptions = {}) {
  const {
    url = 'http://localhost:6969',
    onMessage,
    onConnect,
    onDisconnect,
  } = options;

  const [isConnected, setIsConnected] = useState(false);
  const [lastMessage, setLastMessage] = useState<unknown>(null);
  const stompClientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<Map<string, (message: unknown) => void>>(new Map());
  const stompSubscriptionsRef = useRef<Map<string, { unsubscribe: () => void }>>(new Map());

  const connect = useCallback(() => {
    if (stompClientRef.current?.connected) {
      return;
    }

    try {
      // SockJS requires http/https, not ws/wss
      const normalizedUrl = url.replace(/^ws(s)?:\/\//, 'http$1://').replace(/\/$/, '');
      const socket = new SockJS(`${normalizedUrl}/ws`);
      const client = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          setIsConnected(true);
          console.log('WebSocket connected via STOMP');
          onConnect?.();

          // Re-subscribe to all subscriptions
          subscriptionsRef.current.forEach((callback, destination) => {
            const subscription = client.subscribe(destination, (message: Message) => {
              try {
                const data = JSON.parse(message.body || '{}');
                setLastMessage(data);
                onMessage?.(data);
                callback(data);
              } catch {
                setLastMessage(message.body);
                onMessage?.(message.body);
                callback(message.body);
              }
            });
            stompSubscriptionsRef.current.set(destination, subscription);
          });
        },
        onDisconnect: () => {
          setIsConnected(false);
          console.log('WebSocket disconnected');
          onDisconnect?.();
        },
        onStompError: (frame) => {
          console.warn('STOMP error:', frame);
        },
      });

      stompClientRef.current = client;
      client.activate();
    } catch (error) {
      console.error('WebSocket connection error:', error);
    }
  }, [url, onConnect, onDisconnect, onMessage]);

  const disconnect = useCallback(() => {
    if (stompClientRef.current) {
      stompClientRef.current.deactivate();
      stompClientRef.current = null;
    }
    setIsConnected(false);
    stompSubscriptionsRef.current.clear();
  }, []);

  const send = useCallback((destination: string, body: unknown) => {
    if (stompClientRef.current?.connected) {
      stompClientRef.current.publish({
        destination,
        body: JSON.stringify({ destination, ...(body as object) }),
      });
    } else {
      console.warn('WebSocket not connected, message not sent');
    }
  }, []);

  const subscribe = useCallback((destination: string, callback: (message: unknown) => void) => {
    subscriptionsRef.current.set(destination, callback);

    // If already connected, subscribe immediately
    if (stompClientRef.current?.connected && !stompSubscriptionsRef.current.has(destination)) {
      const subscription = stompClientRef.current.subscribe(destination, (message: Message) => {
        try {
          const data = JSON.parse(message.body || '{}');
          setLastMessage(data);
          onMessage?.(data);
          callback(data);
        } catch {
          setLastMessage(message.body);
          onMessage?.(message.body);
          callback(message.body);
        }
      });
      stompSubscriptionsRef.current.set(destination, subscription);
    }
  }, [onMessage]);

  const unsubscribe = useCallback((destination: string) => {
    subscriptionsRef.current.delete(destination);
    const subscription = stompSubscriptionsRef.current.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      stompSubscriptionsRef.current.delete(destination);
    }
  }, []);

  useEffect(() => {
    return () => {
      disconnect();
    };
  }, [disconnect]);

  return {
    isConnected,
    lastMessage,
    connect,
    disconnect,
    send,
    subscribe,
    unsubscribe,
    client: stompClientRef.current,
  };
}

export function useNotificationSubscription(userEmail: string) {
  const { subscribe, send, isConnected } = useWebSocket({
    url: 'http://localhost:6969',
  });

  useEffect(() => {
    if (isConnected && userEmail) {
      subscribe(`/user/${userEmail}/queue/notifications`, (notification) => {
        console.log('Notification:', notification);
      });
    }
  }, [isConnected, userEmail, subscribe]);

  return { isConnected, send };
}

export function useTicketUpdates() {
  const { subscribe, isConnected } = useWebSocket({
    url: 'http://localhost:6969',
  });

  useEffect(() => {
    if (isConnected) {
      subscribe('/topic/tickets', (update) => {
        window.dispatchEvent(new CustomEvent('ticket-update', { detail: update }));
      });
    }
  }, [isConnected, subscribe]);

  return { isConnected };
}