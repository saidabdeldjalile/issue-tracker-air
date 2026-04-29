package com.suryakn.IssueTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnansweredQuestionDto {
    private Long id;

    @Size(max = 5000, message = "Question must not exceed 5000 characters")
    private String question;

    @Size(max = 2000, message = "Context must not exceed 2000 characters")
    private String context;

    @Email(message = "User email must be valid")
    private String userEmail;

    @Size(max = 100, message = "Suggested category must not exceed 100 characters")
    private String suggestedCategory;

    @Size(max = 100, message = "Suggested department must not exceed 100 characters")
    private String suggestedDepartment;

    private Long relatedTicketId;

    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;

    private String createdAt;
    private String resolvedAt;

    @Size(max = 5000, message = "Answer must not exceed 5000 characters")
    private String answer;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 200, message = "Keywords must not exceed 200 characters")
    private String keywords;

    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    private String rejectionReason;
}