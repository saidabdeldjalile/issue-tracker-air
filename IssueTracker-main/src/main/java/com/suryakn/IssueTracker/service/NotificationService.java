package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.NotificationDto;
import com.suryakn.IssueTracker.entity.*;
import com.suryakn.IssueTracker.repository.NotificationRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    // Map to store SSE emitters for each user (userId -> SseEmitter)
    private final Map<Integer, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    
    /**
     * Create and save a notification for a specific user
     */
    public Notification createNotification(NotificationType type, String title, String message, 
                                            UserEntity user, Department department, Long relatedEntityId) {
        Notification notification = Notification.builder()
                .type(type)
                .title(title)
                .message(message)
                .user(user)
                .department(department)
                .relatedEntityId(relatedEntityId)
                .isRead(false)
                .build();
        
        notification = notificationRepository.save(notification);
        
        // Send real-time notification via SSE if user is connected
        sendRealTimeNotification(user.getId(), notification);
        
        return notification;
    }
    
    /**
     * Create and send notifications to multiple users
     */
    public void createNotificationsForUsers(NotificationType type, String title, String message,
                                           List<UserEntity> users, Department department, Long relatedEntityId) {
        for (UserEntity user : users) {
            createNotification(type, title, message, user, department, relatedEntityId);
        }
    }
    
    /**
     * Send real-time notification to a specific user via SSE
     */
    public void sendRealTimeNotification(Integer userId, Notification notification) {
        SseEmitter emitter = userEmitters.get(userId);
        if (emitter != null) {
            try {
                NotificationDto dto = NotificationDto.fromEntity(notification);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto, MediaType.APPLICATION_JSON));
                log.info("Sent real-time notification to user {}: {}", userId, notification.getTitle());
            } catch (IOException e) {
                log.error("Error sending SSE notification to user {}: {}", userId, e.getMessage());
                userEmitters.remove(userId);
            }
        }
    }
    
    /**
     * Register a new SSE connection for a user
     */
    public SseEmitter registerUserConnection(Integer userId) {
        // Remove existing emitter if any
        userEmitters.remove(userId);
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        userEmitters.put(userId, emitter);
        
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for user {}", userId);
            userEmitters.remove(userId);
        });
        
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for user {}", userId);
            userEmitters.remove(userId);
        });
        
        emitter.onError(e -> {
            log.error("SSE error for user {}: {}", userId, e.getMessage());
            userEmitters.remove(userId);
        });
        
        log.info("Registered SSE connection for user {}", userId);
        return emitter;
    }
    
    /**
     * Get all notifications for a user
     */
    public List<NotificationDto> getNotificationsForUser(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications count for a user
     */
    public Long getUnreadCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * Mark a notification as read
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification notification : notifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(notifications);
    }
    
    /**
     * Notify all users in a department about a project event
     */
    public void notifyDepartmentUsers(NotificationType type, String title, String message, 
                                       Department department, Long projectId) {
        if (department == null || department.getUsers() == null) {
            return;
        }
        
        List<UserEntity> users = department.getUsers();
        createNotificationsForUsers(type, title, message, users, department, projectId);
    }
}

