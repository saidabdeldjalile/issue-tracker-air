package com.suryakn.IssueTracker.dto.dashboard;

import lombok.Data;

@Data
public class DepartmentStats {
    private Long departmentId;
    private String departmentName;
    private Long ticketCount;
    private Long openCount;
    private Long closedCount;
    private Double averageResolutionTime;
}