package com.suryakn.IssueTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String response;
    private String intent;
    private String category;
    private boolean needsTicketCreation;
    private String suggestedCategory;
    private String suggestedPriority;
    private String suggestedDepartment;
    private Long suggestedProjectId;
    private String suggestedProjectName;
    private String knowledgeType;
    private String knowledgeTitle;
    private String sessionId;
    private String draftTitle;
    private String draftDescription;
    private Boolean ticketCreated;
    private Long ticketId;
    private String ticketUrl;
    private Double confidenceScore;
    private Boolean needsEscalation;

    // Additional fields for AI integration
    private String priority;
    private Map<String, List<String>> entities;
    private String sentiment;
    private String urgency;
}
