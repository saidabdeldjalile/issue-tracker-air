package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.ProjectDto;
import com.suryakn.IssueTracker.dto.ProjectRequest;
import com.suryakn.IssueTracker.dto.TicketResponse;
import com.suryakn.IssueTracker.entity.Project;
import com.suryakn.IssueTracker.service.ProjectService;
import com.suryakn.IssueTracker.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getALlProject() {
        return projectService.getAllProjects();
    }

    @GetMapping("by-department/{departmentId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByDepartment(@PathVariable Long departmentId) {
        return projectService.getProjectsByDepartmentId(departmentId);
    }

    @GetMapping("{pid}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long pid) {
        return projectService.getProject(pid);
    }

    @DeleteMapping("{pid}")
    public void deleteProject(@PathVariable Long pid) {
        projectService.deleteProject(pid);
    }

    @PostMapping
    public ResponseEntity<Project> createNewProject(@RequestBody ProjectRequest projectRequest) {
        return projectService.createProject(projectRequest);
    }

    @GetMapping("{pid}/tickets")
    public ResponseEntity<List<TicketResponse>> ticketsWithProjectId(@PathVariable Long pid) {
        return ticketService.getAllTicketByProjectId(pid);
    }

    @GetMapping("{pid}/tickets/{tid}")
    public ResponseEntity<TicketResponse> ticketsWithProjectId(@PathVariable Long pid, @PathVariable Long tid) {
        return ticketService.getTicketByProjectId(pid, tid);
    }
}
