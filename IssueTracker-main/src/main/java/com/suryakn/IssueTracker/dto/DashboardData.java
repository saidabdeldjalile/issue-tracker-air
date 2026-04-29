package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.dto.dashboard.DashboardStats;
import com.suryakn.IssueTracker.dto.dashboard.DepartmentStats;
import com.suryakn.IssueTracker.dto.dashboard.TicketStatusDistribution;
import com.suryakn.IssueTracker.dto.dashboard.UserStats;

import java.util.List;

public class DashboardData {
    private DashboardStats stats;
    private List<TicketStatusDistribution> statusDistribution;
    private List<DepartmentStats> departmentStats;
    private List<UserStats> userStats;
    private TimeRangeFilter timeRange;

    // Constructors
    public DashboardData() {}

    public DashboardData(DashboardStats stats, List<TicketStatusDistribution> statusDistribution, 
                        List<DepartmentStats> departmentStats, List<UserStats> userStats, 
                        TimeRangeFilter timeRange) {
        this.stats = stats;
        this.statusDistribution = statusDistribution;
        this.departmentStats = departmentStats;
        this.userStats = userStats;
        this.timeRange = timeRange;
    }

    // Getters and Setters
    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public List<TicketStatusDistribution> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(List<TicketStatusDistribution> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public List<DepartmentStats> getDepartmentStats() {
        return departmentStats;
    }

    public void setDepartmentStats(List<DepartmentStats> departmentStats) {
        this.departmentStats = departmentStats;
    }

    public List<UserStats> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStats> userStats) {
        this.userStats = userStats;
    }

    public TimeRangeFilter getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRangeFilter timeRange) {
        this.timeRange = timeRange;
    }
}