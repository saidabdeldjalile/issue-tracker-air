package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.NotificationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userEmail, NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                userEmail,
                "/queue/notifications",
                notification
        );
        log.debug("WebSocket notification sent to {}: {}", userEmail, notification.getTitle());
    }

    public void broadcastNotification(String topic, Object message) {
        messagingTemplate.convertAndSend(topic, message);
        log.debug("Broadcast sent to topic {}: {}", topic, message);
    }

    public void notifyTicketUpdate(Long ticketId, String action, String userEmail) {
        messagingTemplate.convertAndSend("/topic/tickets", TicketUpdate.builder()
                .ticketId(ticketId)
                .action(action)
                .userEmail(userEmail)
                .build());
    }

    public void notifyDepartmentUpdate(Long departmentId, String action) {
        messagingTemplate.convertAndSend("/topic/departments", DepartmentUpdate.builder()
                .departmentId(departmentId)
                .action(action)
                .build());
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class TicketUpdate {
        private Long ticketId;
        private String action;
        private String userEmail;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class DepartmentUpdate {
        private Long departmentId;
        private String action;
    }
}