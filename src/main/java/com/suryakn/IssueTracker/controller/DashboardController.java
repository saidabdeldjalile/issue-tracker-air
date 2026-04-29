package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.DashboardData;
import com.suryakn.IssueTracker.dto.TimeRangeFilter;
import com.suryakn.IssueTracker.dto.dashboard.DepartmentStats;
import com.suryakn.IssueTracker.dto.dashboard.TicketStatusDistribution;
import com.suryakn.IssueTracker.dto.dashboard.UserStats;
import com.suryakn.IssueTracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    // Get overall dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<DashboardData> getStats() {
        return dashboardService.getStats();
    }

    // Get dashboard data with optional filters
    @GetMapping("/data")
    public ResponseEntity<DashboardData> getDashboardData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId) {
        
        TimeRangeFilter filters = new TimeRangeFilter();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setDepartmentId(departmentId);
        filters.setProjectId(projectId);
        
        return dashboardService.getDashboardData(filters);
    }

    // Get ticket status distribution
    @GetMapping("/status-distribution")
    public ResponseEntity<List<TicketStatusDistribution>> getStatusDistribution(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId) {
        
        TimeRangeFilter filters = new TimeRangeFilter();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setDepartmentId(departmentId);
        filters.setProjectId(projectId);
        
        return dashboardService.getStatusDistribution(filters);
    }

    // Get department statistics
    @GetMapping("/department-stats")
    public ResponseEntity<List<DepartmentStats>> getDepartmentStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId) {
        
        TimeRangeFilter filters = new TimeRangeFilter();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setDepartmentId(departmentId);
        filters.setProjectId(projectId);
        
        return dashboardService.getDepartmentStats(filters);
    }

    // Get user statistics
    @GetMapping("/user-stats")
    public ResponseEntity<List<UserStats>> getUserStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId) {
        
        TimeRangeFilter filters = new TimeRangeFilter();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setDepartmentId(departmentId);
        filters.setProjectId(projectId);
        
        return dashboardService.getUserStats(filters);
    }

    // Export dashboard data
    @GetMapping("/export/{format}")
    public ResponseEntity<byte[]> exportData(
            @PathVariable String format,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId) {
        
        TimeRangeFilter filters = new TimeRangeFilter();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setDepartmentId(departmentId);
        filters.setProjectId(projectId);
        
        return dashboardService.exportData(format, filters);
    }
}