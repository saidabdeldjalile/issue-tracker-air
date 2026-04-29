package com.suryakn.IssueTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 2000, message = "Message must be between 1 and 2000 characters")
    private String message;

    private Long userId;

    @Email(message = "User email must be valid")
    private String userEmail;

    private String sessionId;
    private Boolean createTicket;
    private String title;
    private String description;
    private String category;
    private String priority;
    private Long projectId;
}
