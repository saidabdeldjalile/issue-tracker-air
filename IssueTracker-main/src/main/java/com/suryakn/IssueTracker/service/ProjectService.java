package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.ProjectDto;
import com.suryakn.IssueTracker.dto.ProjectRequest;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.NotificationType;
import com.suryakn.IssueTracker.entity.Project;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import com.suryakn.IssueTracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final NotificationService notificationService;

    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<Project> projectList = projectRepository.findAll();
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for (Project project : projectList) {
            projectDtoList.add(new ProjectDto(project));
        }
        return ResponseEntity.ok(projectDtoList);
    }

    public ResponseEntity<List<ProjectDto>> getProjectsByDepartmentId(Long departmentId) {
        List<Project> projectList = projectRepository.findByDepartmentId(departmentId);
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for (Project project : projectList) {
            projectDtoList.add(new ProjectDto(project));
        }
        return ResponseEntity.ok(projectDtoList);
    }

    public ResponseEntity<Project> createProject(ProjectRequest projectRequest) {
        Department department = null;
        if (projectRequest.getDepartmentId() != null) {
            department = departmentRepository.findById(projectRequest.getDepartmentId()).orElse(null);
        }
        Project project = Project.builder()
                .name(projectRequest.getName())
                .department(department)
                .build();
        Project savedProject = projectRepository.save(project);
        
        // Notify all users in the department about the new project
        if (department != null) {
            notificationService.notifyDepartmentUsers(
                NotificationType.PROJECT_CREATED,
                "Nouveau projet ajouté",
                "Le projet '" + project.getName() + "' a été ajouté au département " + department.getName(),
                department,
                project.getId()
            );
            log.info("Notifications sent to department {} for new project {}", department.getName(), project.getName());
        }
        
        return ResponseEntity.ok(savedProject);
    }

    public ResponseEntity<ProjectDto> getProject(Long pid) {
        return ResponseEntity.ok(new ProjectDto(projectRepository.findById(pid).orElseThrow()));
    }

    public void deleteProject(Long pid) {
        Project project = projectRepository.findById(pid).orElse(null);
        Department department = project != null ? project.getDepartment() : null;
        
        projectRepository.deleteById(pid);
        
        // Notify all users in the department about the deleted project
        if (department != null) {
            notificationService.notifyDepartmentUsers(
                NotificationType.PROJECT_DELETED,
                "Projet supprimé",
                "Le projet '" + (project != null ? project.getName() : "") + "' a été supprimé du département " + department.getName(),
                department,
                pid
            );
            log.info("Notifications sent to department {} for deleted project {}", department.getName(), pid);
        }
    }
}
