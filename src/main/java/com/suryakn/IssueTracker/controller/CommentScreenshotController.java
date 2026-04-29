package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.ScreenshotDto;
import com.suryakn.IssueTracker.service.CommentScreenshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets/{ticketId}/comments/{commentId}/screenshots")
@Slf4j
public class CommentScreenshotController {

    private final CommentScreenshotService commentScreenshotService;

    @GetMapping
    public ResponseEntity<List<ScreenshotDto>> getScreenshots(
            @PathVariable Long ticketId,
            @PathVariable Long commentId) {
        log.info("Getting screenshots for comment {} in ticket {}", commentId, ticketId);
        return commentScreenshotService.getScreenshotsByCommentId(commentId);
    }

    @PostMapping
    public ResponseEntity<ScreenshotDto> uploadScreenshot(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        log.info("Uploading screenshot for comment {} in ticket {}", commentId, ticketId);
        String email = authentication.getName();
        return commentScreenshotService.uploadScreenshot(commentId, file, email);
    }

    @DeleteMapping("{screenshotId}")
    public ResponseEntity<String> deleteScreenshot(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            @PathVariable Long screenshotId,
            Authentication authentication) {
        
        log.info("Deleting screenshot {} from comment {} in ticket {}", screenshotId, commentId, ticketId);
        String email = authentication.getName();
        return commentScreenshotService.deleteScreenshot(screenshotId, email);
    }
}