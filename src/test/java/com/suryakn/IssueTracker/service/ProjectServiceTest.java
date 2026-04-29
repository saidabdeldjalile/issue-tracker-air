package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.ProjectDto;
import com.suryakn.IssueTracker.dto.ProjectRequest;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.Project;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import com.suryakn.IssueTracker.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("IT Department")
                .description("Information Technology")
                .build();

        testProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .department(testDepartment)
                .build();
    }

    @Test
    void testGetAllProjects_WithoutSearch_ShouldReturnAllProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(testProject);
        Page<Project> projectPage = new PageImpl<>(projects);
        when(projectRepository.findAll(any(Pageable.class))).thenReturn(projectPage);

        ResponseEntity<Page<ProjectDto>> response = projectService.getAllProjects(
            PageRequest.of(0, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Test Project", response.getBody().getContent().iterator().next().getName());
    }

    @Test
    void testGetAllProjects_WithSearch_ShouldReturnFilteredProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(testProject);
        Page<Project> projectPage = new PageImpl<>(projects);
        when(projectRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(projectPage);

        ResponseEntity<Page<ProjectDto>> response = projectService.getAllProjects(
            PageRequest.of(0, 10), "Test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectRepository, times(1)).findByNameContainingIgnoreCase("Test", PageRequest.of(0, 10));
    }

    @Test
    void testGetProjectsByDepartmentId_WithoutSearch_ShouldReturnDepartmentProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(testProject);
        when(projectRepository.findByDepartmentId(1L)).thenReturn(projects);

        ResponseEntity<Page<ProjectDto>> response = projectService.getProjectsByDepartmentId(
            1L, PageRequest.of(0, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testGetProjectsByDepartmentId_WithSearch_ShouldReturnFilteredProjects() {
        List<Project> projects = new ArrayList<>();
        projects.add(testProject);
        Page<Project> projectPage = new PageImpl<>(projects);
        when(projectRepository.findByDepartmentIdAndNameContainingIgnoreCase(eq(1L), anyString(), any(Pageable.class)))
            .thenReturn(projectPage);

        ResponseEntity<Page<ProjectDto>> response = projectService.getProjectsByDepartmentId(
            1L, PageRequest.of(0, 10), "Test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectRepository, times(1)).findByDepartmentIdAndNameContainingIgnoreCase(1L, "Test", PageRequest.of(0, 10));
    }

    @Test
    void testGetProjectsByDepartmentId_WithPagination_ShouldReturnPaginatedResults() {
        List<Project> projects = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            projects.add(Project.builder()
                .id((long) i)
                .name("Project " + i)
                .department(testDepartment)
                .build());
        }
        when(projectRepository.findByDepartmentId(1L)).thenReturn(projects);

        ResponseEntity<Page<ProjectDto>> response = projectService.getProjectsByDepartmentId(
            1L, PageRequest.of(0, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getContent().size());
        assertEquals(25L, response.getBody().getTotalElements());
    }

    @Test
    void testGetProjectsByDepartmentId_WithPaginationPage2_ShouldReturnCorrectPage() {
        List<Project> projects = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            projects.add(Project.builder()
                .id((long) i)
                .name("Project " + i)
                .department(testDepartment)
                .build());
        }
        when(projectRepository.findByDepartmentId(1L)).thenReturn(projects);

        ResponseEntity<Page<ProjectDto>> response = projectService.getProjectsByDepartmentId(
            1L, PageRequest.of(2, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getContent().size());
    }

    @Test
    void testGetProjectsByDepartmentId_WithEmptyList_ShouldReturnEmptyPage() {
        when(projectRepository.findByDepartmentId(1L)).thenReturn(new ArrayList<>());

        ResponseEntity<Page<ProjectDto>> response = projectService.getProjectsByDepartmentId(
            1L, PageRequest.of(0, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getContent().size());
    }

    @Test
    void testCreateProject_WithValidData_ShouldReturnCreatedProject() {
        ProjectRequest request = ProjectRequest.builder()
                .name("New Project")
                .departmentId(1L)
                .build();

        // Create a project that will be returned by save (with generated ID)
        Project savedProject = Project.builder()
                .id(1L)
                .name("New Project")
                .department(testDepartment)
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ResponseEntity<Project> response = projectService.createProject(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Project", response.getBody().getName());
        verify(projectRepository, times(1)).save(any(Project.class));
        // After the fix, notification is called with the saved project's ID
        verify(notificationService, times(1)).notifyDepartmentUsers(
            any(), anyString(), anyString(), eq(testDepartment), eq(1L)
        );
    }

    @Test
    void testCreateProject_WithoutDepartment_ShouldCreateProject() {
        ProjectRequest request = ProjectRequest.builder()
                .name("New Project")
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        ResponseEntity<Project> response = projectService.createProject(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, never()).notifyDepartmentUsers(any(), anyString(), anyString(), any(), anyLong());
    }

    @Test
    void testGetProject_WhenExists_ShouldReturnProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        ResponseEntity<ProjectDto> response = projectService.getProject(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Project", response.getBody().getName());
    }

    @Test
    void testGetProject_WhenNotExists_ShouldThrowException() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> projectService.getProject(999L));
    }

    @Test
    void testDeleteProject_WhenExists_ShouldDeleteProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).deleteById(1L);
        verify(notificationService, times(1)).notifyDepartmentUsers(any(), anyString(), anyString(), any(), anyLong());
    }

    @Test
    void testDeleteProject_WhenNotExists_ShouldStillCallDeleteById() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        projectService.deleteProject(999L);

        verify(projectRepository, times(1)).deleteById(999L);
        verify(notificationService, never()).notifyDepartmentUsers(any(), anyString(), anyString(), any(), anyLong());
    }

    @Test
    void testDeleteProject_WithNullDepartment_ShouldNotNotify() {
        Project projectWithoutDept = Project.builder()
                .id(2L)
                .name("Project without department")
                .build();

        when(projectRepository.findById(2L)).thenReturn(Optional.of(projectWithoutDept));

        projectService.deleteProject(2L);

        verify(projectRepository, times(1)).deleteById(2L);
        verify(notificationService, never()).notifyDepartmentUsers(any(), anyString(), anyString(), any(), anyLong());
    }
}