package com.suryakn.IssueTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String comment;
    private String email;
    private String username;
    private LocalDateTime created;
    private String role; // Role of the commenter (ADMIN, SUPPORT, USER)
}
