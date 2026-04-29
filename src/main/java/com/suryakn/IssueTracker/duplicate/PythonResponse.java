package com.suryakn.IssueTracker.duplicate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PythonResponse {
    private List<Long> similar_ticket_ids;
    private String vector;
}
