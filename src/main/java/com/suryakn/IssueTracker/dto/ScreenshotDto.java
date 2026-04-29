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
public class ScreenshotDto {
    private Long id;
    private String imageUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime createdAt;
    private String uploadedByEmail;
    private String uploadedByName;
}