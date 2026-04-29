package com.suryakn.IssueTracker.classification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationResponse {
    private String category;
    private String suggestedDepartment;
    private String suggestedPriority;
    private java.util.List<String> keywords;
    private Double confidence;
    private java.util.List<Double> embedding;
}