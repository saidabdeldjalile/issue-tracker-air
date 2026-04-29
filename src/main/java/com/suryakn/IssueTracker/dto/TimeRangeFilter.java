package com.suryakn.IssueTracker.dto;

import lombok.Data;

@Data
public class TimeRangeFilter {
    private String startDate;
    private String endDate;
    private Long departmentId;
    private Long projectId;
}