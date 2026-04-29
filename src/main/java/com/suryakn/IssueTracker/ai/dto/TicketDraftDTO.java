package com.suryakn.IssueTracker.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDraftDTO {
    private String title;
    private String description;
    private String category;
    private String priority;
    private String suggestedDepartment;
    private Long projectId;
    private String projectName;
}
