package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.ScreenshotDto;
import com.suryakn.IssueTracker.entity.Comment;
import com.suryakn.IssueTracker.entity.CommentScreenshot;
import com.suryakn.IssueTracker.entity.UserEntity;
import com.suryakn.IssueTracker.repository.CommentRepository;
import com.suryakn.IssueTracker.repository.CommentScreenshotRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentScreenshotService {

    private final CommentScreenshotRepository commentScreenshotRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.path:uploads/screenshots}")
    private String uploadPath;

    public ResponseEntity<List<ScreenshotDto>> getScreenshotsByCommentId(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<CommentScreenshot> screenshots = commentScreenshotRepository.findByCommentId(commentId);
        List<ScreenshotDto> dtos = new ArrayList<>();
        
        for (CommentScreenshot screenshot : screenshots) {
            dtos.add(mapToDto(screenshot));
        }
        
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    public ResponseEntity<ScreenshotDto> uploadScreenshot(Long commentId, MultipartFile file, String email) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + "_" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
                             extension;
            
            // Save file
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Create screenshot entity
            Comment comment = optionalComment.get();
            UserEntity user = optionalUser.get();
            
            CommentScreenshot screenshot = CommentScreenshot.builder()
                    .imageUrl("/api/screenshots/" + fileName)
                    .fileName(originalFileName != null ? originalFileName : fileName)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .comment(comment)
                    .uploadedBy(user)
                    .build();

            CommentScreenshot savedScreenshot = commentScreenshotRepository.save(screenshot);
            
            log.info("Comment screenshot uploaded: {} for comment {}", fileName, commentId);
            
            return new ResponseEntity<>(mapToDto(savedScreenshot), HttpStatus.CREATED);
            
        } catch (IOException e) {
            log.error("Error uploading comment screenshot: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteScreenshot(Long screenshotId, String email) {
        Optional<CommentScreenshot> optionalScreenshot = commentScreenshotRepository.findById(screenshotId);
        if (optionalScreenshot.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CommentScreenshot screenshot = optionalScreenshot.get();
        
        // Check if user is the uploader or has permission to delete
        if (!screenshot.getUploadedBy().getEmail().equals(email)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            // Delete file from filesystem
            String imageUrl = screenshot.getImageUrl();
            if (imageUrl != null && imageUrl.startsWith("/api/screenshots/")) {
                String fileName = imageUrl.substring("/api/screenshots/".length());
                Path filePath = Paths.get(uploadPath).resolve(fileName);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }

            // Delete from database
            commentScreenshotRepository.delete(screenshot);
            log.info("Comment screenshot deleted: {}", screenshotId);
            
            return new ResponseEntity<>("Comment screenshot deleted successfully", HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("Error deleting comment screenshot file: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ScreenshotDto mapToDto(CommentScreenshot screenshot) {
        return ScreenshotDto.builder()
                .id(screenshot.getId())
                .imageUrl(screenshot.getImageUrl())
                .fileName(screenshot.getFileName())
                .fileSize(screenshot.getFileSize())
                .mimeType(screenshot.getMimeType())
                .createdAt(screenshot.getCreatedAt())
                .uploadedByEmail(screenshot.getUploadedBy().getEmail())
                .uploadedByName(screenshot.getUploadedBy().getFirstName() + " " + 
                               screenshot.getUploadedBy().getLastName())
                .build();
    }
}