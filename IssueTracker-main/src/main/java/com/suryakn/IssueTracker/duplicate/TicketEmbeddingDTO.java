package com.suryakn.IssueTracker.duplicate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEmbeddingDTO {
    private Long ticketId;
    private String vector;
}
