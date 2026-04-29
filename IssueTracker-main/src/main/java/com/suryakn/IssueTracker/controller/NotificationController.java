package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.NotificationDto;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.UserRepository;
import com.suryakn.IssueTracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    /**
     * SSE endpoint for real-time notifications
     * Connect with: EventSource with URL "/api/notifications/stream?email=user@email.com"
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new SseEmitter();
        }
        
        SseEmitter emitter = notificationService.registerUserConnection(user.getId());
        
        // Send initial connection message
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to notification stream"));
        } catch (IOException e) {
            // Connection might be closed immediately
        }
        
        return emitter;
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

