package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.DashboardData;
import com.suryakn.IssueTracker.dto.TimeRangeFilter;
import com.suryakn.IssueTracker.dto.dashboard.DepartmentStats;
import com.suryakn.IssueTracker.dto.dashboard.TicketStatusDistribution;
import com.suryakn.IssueTracker.dto.dashboard.UserStats;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DashboardService {
    
    ResponseEntity<DashboardData> getStats();
    
    ResponseEntity<DashboardData> getDashboardData(TimeRangeFilter filters);
    
    ResponseEntity<List<TicketStatusDistribution>> getStatusDistribution(TimeRangeFilter filters);
    
    ResponseEntity<List<DepartmentStats>> getDepartmentStats(TimeRangeFilter filters);
    
    ResponseEntity<List<UserStats>> getUserStats(TimeRangeFilter filters);
    
    ResponseEntity<byte[]> exportData(String format, TimeRangeFilter filters);
}