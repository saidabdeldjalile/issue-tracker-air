package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.entity.Project;
import org.hibernate.Hibernate;
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
        if (project == null) {
            return;
        }
        this.id = project.getId();
        this.name = project.getName();
        
        // Handle lazy loading - use Hibernate.isInitialized() to check if department is loaded
        if (project.getDepartment() != null) {
            try {
                // Check if the entity is initialized (not lazy loaded)
        if (Hibernate.isInitialized(project.getDepartment())) {
                    this.departmentId = project.getDepartment().getId();
                    this.departmentName = project.getDepartment().getName();
                } else {
                    // If not initialized, try to access to trigger initialization within transaction
                    // This will work if called within an active transaction
                    try {
                        this.departmentId = project.getDepartment().getId();
                        this.departmentName = project.getDepartment().getName();
                    } catch (Exception e) {
                        // If still fails, leave as null
                        this.departmentId = null;
                        this.departmentName = null;
                    }
                }
            } catch (Exception e) {
                // Handle any lazy loading exceptions
                this.departmentId = null;
                this.departmentName = null;
            }
        }
    }
}
