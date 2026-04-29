package com.suryakn.IssueTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String registrationNumber;
}
