package com.suryakn.IssueTracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkActionRequest {
    @NotNull(message = "Ticket IDs list is required")
    @NotEmpty(message = "Ticket IDs list cannot be empty")
    private List<Long> ticketIds;

    @Email(message = "Assignee email must be valid")
    private String assigneeEmail;
}