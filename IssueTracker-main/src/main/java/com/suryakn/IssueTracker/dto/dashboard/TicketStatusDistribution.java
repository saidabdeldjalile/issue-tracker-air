package com.suryakn.IssueTracker.dto.dashboard;

import lombok.Data;

@Data
public class TicketStatusDistribution {
    private String status;
    private Long count;
    private Double percentage;
}