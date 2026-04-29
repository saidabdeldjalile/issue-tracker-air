package com.suryakn.IssueTracker.dto;

import jakarta.validation.constraints.NotBlank;
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
public class FAQDTO {
    private Long id;

    @NotBlank(message = "Question is required")
    @Size(min = 1, max = 1000, message = "Question must be between 1 and 1000 characters")
    private String question;

    @NotBlank(message = "Answer is required")
    @Size(min = 1, max = 5000, message = "Answer must be between 1 and 5000 characters")
    private String answer;

    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    private String category;

    private List<String> keywords;
    private Long departmentId;
    private String departmentName;
    private boolean active;
    private Integer viewCount;
    private String createdAt;
    private String modifiedAt;
}