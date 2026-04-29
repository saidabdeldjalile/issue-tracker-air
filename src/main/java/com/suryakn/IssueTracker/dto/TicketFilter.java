package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.entity.Priority;
import com.suryakn.IssueTracker.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketFilter {
    private String search;
    private List<Status> statuses;
    private List<Priority> priorities;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long departmentId;
    private Long projectId;
    private String assigneeEmail;
    private String reporterEmail;
    private String category;
}