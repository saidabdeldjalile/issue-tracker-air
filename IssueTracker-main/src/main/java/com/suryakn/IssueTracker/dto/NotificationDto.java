package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.entity.Notification;
import com.suryakn.IssueTracker.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Integer userId;
    private Long departmentId;
    private String departmentName;
    private Long relatedEntityId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationDto fromEntity(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .departmentId(notification.getDepartment() != null ? notification.getDepartment().getId() : null)
                .departmentName(notification.getDepartment() != null ? notification.getDepartment().getName() : null)
                .relatedEntityId(notification.getRelatedEntityId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

