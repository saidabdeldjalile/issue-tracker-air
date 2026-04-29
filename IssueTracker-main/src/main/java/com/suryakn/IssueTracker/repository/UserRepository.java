package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.dto.UserProjection;
import com.suryakn.IssueTracker.entity.Department;
import com.suryakn.IssueTracker.entity.Role;
import com.suryakn.IssueTracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    // Changed to return a list to handle duplicate emails gracefully
    List<UserEntity> findAllByEmail(String email);
    
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);

    boolean existsByRegistrationNumber(String registrationNumber);
    
    Optional<UserEntity> findByRegistrationNumber(String registrationNumber);

    List<UserProjection> findAllBy();

    // Find users by department and role (for auto-assignment to manager)
    List<UserEntity> findByDepartmentAndRole(Department department, Role role);
    
    // Find all users by role and department (for notifications)
    List<UserEntity> findAllByRoleAndDepartment(Role role, Department department);
}
