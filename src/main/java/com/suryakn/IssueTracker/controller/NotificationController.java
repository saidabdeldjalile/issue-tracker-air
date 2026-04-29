package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.NotificationDto;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.UserRepository;
import com.suryakn.IssueTracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    /**
     * SSE endpoint for real-time notifications
     * Connect with: EventSource with URL "/api/notifications/stream?email=user@email.com"
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamNotifications(@RequestParam String email) {
        log.info("SSE stream request for email: {}", email);
        
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.warn("SSE connection rejected - User not found: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("SSE-Error", "User not found: " + email)
                    .build();
        }
        
        SseEmitter emitter = notificationService.registerUserConnection(user.getId());
        
        // Send initial connection message
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to notification stream for user: " + user.getId()));
            log.info("SSE connected for user: {} (email: {})", user.getId(), email);
        } catch (IOException e) {
            log.warn("Failed to send initial SSE message for user {}: {}", user.getId(), e.getMessage());
        }
        
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("X-Accel-Buffering", "no")
                .body(emitter);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "notifications"));
    }

    /**
     * Get all notifications for a user
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<NotificationDto> notifications = notificationService.getNotificationsForUser(user.getId());
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Get unread notifications count for a user
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    /**
     * Mark a notification as read
     */
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }
}