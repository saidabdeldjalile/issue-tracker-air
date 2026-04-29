package com.suryakn.IssueTracker.duplicate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateTicketRequest {
    private Long ticketId;
    private Long projectId;
    private String title;
    private String description;
}
