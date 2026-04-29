package com.suryakn.IssueTracker.entity;

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
@NoArgsConstructor
@AllArgsConstructor
public class UnansweredQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(nullable = false)
    private String userEmail;

    private String suggestedCategory;

    private String suggestedDepartment;

    private Long relatedTicketId;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private Long resolvedByFaqId;

    public enum QuestionStatus {
        PENDING,
        REVIEWED,
        ADDED_TO_FAQ,
        REJECTED
    }
}