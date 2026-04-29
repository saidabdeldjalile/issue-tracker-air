export enum NotificationType {
  PROJECT_CREATED = 'PROJECT_CREATED',
  PROJECT_DELETED = 'PROJECT_DELETED',
  TICKET_CREATED = 'TICKET_CREATED',
  TICKET_STATUS_CHANGED = 'TICKET_STATUS_CHANGED',
  TICKET_ASSIGNED = 'TICKET_ASSIGNED',
  TICKET_DELETED = 'TICKET_DELETED'
}

export interface Notification {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  userId: number | null;
  departmentId: number | null;
  departmentName: string | null;
  relatedEntityId: number | null;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationResponse {
  count: number;
}

