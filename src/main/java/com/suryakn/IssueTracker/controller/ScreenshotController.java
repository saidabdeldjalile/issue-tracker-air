package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.ScreenshotDto;
import com.suryakn.IssueTracker.service.ScreenshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets/{ticketId}/screenshots")
@Slf4j
public class ScreenshotController {

    private final ScreenshotService screenshotService;

    @GetMapping
    public ResponseEntity<List<ScreenshotDto>> getScreenshots(@PathVariable Long ticketId) {
        log.info("Getting screenshots for ticket {}", ticketId);
        return screenshotService.getScreenshotsByTicketId(ticketId);
    }

    @PostMapping
    public ResponseEntity<ScreenshotDto> uploadScreenshot(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        log.info("Uploading screenshot for ticket {}", ticketId);
        String email = authentication.getName();
        return screenshotService.uploadScreenshot(ticketId, file, email);
    }

    @DeleteMapping("{screenshotId}")
    public ResponseEntity<String> deleteScreenshot(
            @PathVariable Long ticketId,
            @PathVariable Long screenshotId,
            Authentication authentication) {
        
        log.info("Deleting screenshot {} from ticket {}", screenshotId, ticketId);
        String email = authentication.getName();
        return screenshotService.deleteScreenshot(screenshotId, email);
    }
}