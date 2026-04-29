package com.suryakn.IssueTracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_feedback")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String userEmail;
    private String message;
    private String response;
    private Integer rating; // 1-5 stars
    private String feedback; // Optional text feedback
    private Boolean helpful;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}