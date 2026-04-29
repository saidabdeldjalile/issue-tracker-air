package com.suryakn.IssueTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotFeedbackDto {
    private Long id;
    private String sessionId;
    private String userEmail;
    private String message;
    private String response;
    private Integer rating;
    private String feedback;
    private Boolean helpful;
    private LocalDateTime createdAt;
}