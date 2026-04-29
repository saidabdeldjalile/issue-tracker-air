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
public class TicketUpdateRequest {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String category;
    private String assignee;
    
    // Modifier information for notifications
    private String modifierEmail;
    private String modifierRole;
}
