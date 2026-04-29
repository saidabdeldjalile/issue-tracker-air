package com.suryakn.IssueTracker.dto.dashboard;

import lombok.Data;

@Data
public class UserStats {
    private Long userId;
    private String userName;
    private Long ticketCount;
    private Long assignedCount;
    private Long resolvedCount;
    private Double averageResolutionTime;
}