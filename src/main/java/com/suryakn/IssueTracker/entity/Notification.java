package com.suryakn.IssueTracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "role", "comments", "createdTickets", "assignedTickets"})
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"projects", "users"})
    private Department department;

    private Long relatedEntityId; // ID of the related ticket/project

    @Builder.Default
    private Boolean isRead = false;

    @CreatedDate
    private LocalDateTime createdAt;

    public Notification(NotificationType type, String title, String message, UserEntity user, Department department, Long relatedEntityId) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.user = user;
        this.department = department;
        this.relatedEntityId = relatedEntityId;
        this.isRead = false;
    }
}

