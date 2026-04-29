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
public class AIChatRequest {
    private String message;
    private String sessionId;
    private String userEmail;
    private Map<String, Object> databaseContext;
}
