package com.suryakn.IssueTracker.ai.dto;

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
public class AIChatResponse {
    private String response;
    private String sessionId;
    private String intent;
    private Double intentConfidence;
    private String category;
    private String suggestedDepartment;
    private String priority;
    private SentimentDTO sentiment;
    private Map<String, List<String>> entities;
    private String waitingFor;
    private TicketDraftDTO ticketDraft;
    private Boolean needsTicketCreation;
    private Boolean needsEscalation;
    private String knowledgeType;
    private String knowledgeTitle;
    private Boolean ticketCreated;
    private Long ticketId;
    private String ticketUrl;
    private Double confidenceScore;
}
