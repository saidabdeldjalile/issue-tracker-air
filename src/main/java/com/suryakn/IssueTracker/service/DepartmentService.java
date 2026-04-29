package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<Department>> getAllDepartments(Pageable pageable, String search) {
        try {
            Page<Department> departmentPage;
            if (search != null && !search.isBlank()) {
                departmentPage = departmentRepository.findByNameContainingIgnoreCase(search, pageable);
            } else {
                departmentPage = departmentRepository.findAll(pageable);
            }
            return ResponseEntity.ok(departmentPage);
        } catch (Exception e) {
            log.error("Error fetching all departments: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Department> getDepartment(Long id) {
        try {
            return departmentRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        log.warn("Department not found with id: {}", id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    });
        } catch (Exception e) {
            log.error("Error fetching department with id {}: ", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Department> createDepartment(Department department) {
        try {
            return ResponseEntity.ok(departmentRepository.save(department));
        } catch (Exception e) {
            log.error("Error creating department: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public void deleteDepartment(Long id) {
        try {
            departmentRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting department with id {}: ", id, e);
            throw new RuntimeException("Failed to delete department", e);
        }
    }
}
