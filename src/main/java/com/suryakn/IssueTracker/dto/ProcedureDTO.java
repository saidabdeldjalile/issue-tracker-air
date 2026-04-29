package com.suryakn.IssueTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 20000, message = "Content must be between 1 and 20000 characters")
    private String content;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Long departmentId;
    private String departmentName;
    private boolean active;
    private String documentPath;
    private String createdAt;
    private String modifiedAt;
}