package com.suryakn.IssueTracker.dto;

import com.suryakn.IssueTracker.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProjection {
    public Integer id;
    public String firstName;
    public String lastName;
    public String email;
    public String role;
    public Long departmentId;
    public String departmentName;
    public String registrationNumber;

    public UserProjection(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.email = userEntity.getEmail();
        this.role = userEntity.getRole() != null ? userEntity.getRole().name() : null;
        this.registrationNumber = userEntity.getRegistrationNumber();
        if (userEntity.getDepartment() != null) {
            this.departmentId = userEntity.getDepartment().getId();
            this.departmentName = userEntity.getDepartment().getName();
        }
    }
}
