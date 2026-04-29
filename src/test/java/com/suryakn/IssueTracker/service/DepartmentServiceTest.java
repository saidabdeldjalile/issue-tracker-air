package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .name("IT Department")
                .description("Information Technology")
                .build();
    }

    @Test
    void testGetAllDepartments_WithoutSearch_ShouldReturnAllDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(testDepartment);
        Page<Department> departmentPage = new PageImpl<>(departments);
        when(departmentRepository.findAll(any(Pageable.class))).thenReturn(departmentPage);

        ResponseEntity<Page<Department>> response = departmentService.getAllDepartments(
            PageRequest.of(0, 10), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testGetAllDepartments_WithSearch_ShouldReturnFilteredDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(testDepartment);
        Page<Department> departmentPage = new PageImpl<>(departments);
        when(departmentRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
            .thenReturn(departmentPage);

        ResponseEntity<Page<Department>> response = departmentService.getAllDepartments(
            PageRequest.of(0, 10), "IT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(departmentRepository, times(1)).findByNameContainingIgnoreCase("IT", PageRequest.of(0, 10));
    }

    @Test
    void testGetDepartment_WhenExists_ShouldReturnDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

        ResponseEntity<Department> response = departmentService.getDepartment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("IT Department", response.getBody().getName());
    }

    @Test
    void testGetDepartment_WhenNotExists_ShouldReturnNotFound() {
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Department> response = departmentService.getDepartment(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateDepartment_WithValidData_ShouldReturnCreatedDepartment() {
        when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

        ResponseEntity<Department> response = departmentService.createDepartment(testDepartment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(departmentRepository, times(1)).save(testDepartment);
    }

    @Test
    void testDeleteDepartment_WhenExists_ShouldDeleteDepartment() {
        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).deleteById(1L);
    }
}