package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private Long departmentId;
    private String departmentName;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        if (project.getDepartment() != null) {
            this.departmentId = project.getDepartment().getId();
            this.departmentName = project.getDepartment().getName();
        }
    }
}
