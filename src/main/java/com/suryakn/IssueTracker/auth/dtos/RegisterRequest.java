package com.suryakn.IssueTracker.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;

    @NotBlank(message = "Registration number is required")
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    private String registrationNumber;
}
