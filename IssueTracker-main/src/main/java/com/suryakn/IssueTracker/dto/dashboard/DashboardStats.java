package com.suryakn.IssueTracker.dto.dashboard;

import lombok.Data;

@Data
public class DashboardStats {
    private Long totalTickets;
    private Long openTickets;
    private Long closedTickets;
    private Long inProgressTickets;
    private Long totalProjects;
    private Long totalUsers;
    private Long totalDepartments;
    private Double averageResolutionTime;
}