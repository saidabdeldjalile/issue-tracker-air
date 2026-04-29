package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.entity.Priority;
import com.suryakn.IssueTracker.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String category;
    private String reporter;
    private String assignee;
    private String issueType;
    private Long project;
}
