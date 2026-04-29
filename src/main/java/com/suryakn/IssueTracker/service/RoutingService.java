package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutingService {

    private final DepartmentRepository departmentRepository;

    private static final Map<String, String> CATEGORY_TO_DEPARTMENT = new HashMap<>();
    
    static {
        CATEGORY_TO_DEPARTMENT.put("informatique", "IT");
        CATEGORY_TO_DEPARTMENT.put("materiel", "Achats");
        CATEGORY_TO_DEPARTMENT.put("administratif", "RH");
        CATEGORY_TO_DEPARTMENT.put("maintenance", "Maintenance");
        CATEGORY_TO_DEPARTMENT.put("achat", "Achats");
        CATEGORY_TO_DEPARTMENT.put("formation", "RH");
        CATEGORY_TO_DEPARTMENT.put("autres", "Support");
    }

    public Optional<Department> findDepartmentByClassification(String category) {
        if (category == null || category.isEmpty()) {
            return Optional.empty();
        }
        
        String departmentName = CATEGORY_TO_DEPARTMENT.getOrDefault(category.toLowerCase(), "Support");
        
        log.debug("Routing category '{}' to department '{}'", category, departmentName);
        
        try {
            Optional<Department> dept = departmentRepository.findByName(departmentName);
            if (dept.isPresent()) {
                return dept;
            }
        } catch (Exception e) {
            log.warn("Failed to find department '{}': {}", departmentName, e.getMessage());
        }
        
        // Fallback: find first available department
        return departmentRepository.findAll().stream().findFirst();
    }

    public String getSuggestedDepartmentName(String category) {
        if (category == null || category.isEmpty()) {
            return "Support";
        }
        return CATEGORY_TO_DEPARTMENT.getOrDefault(category.toLowerCase(), "Support");
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}