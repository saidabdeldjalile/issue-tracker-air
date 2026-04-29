package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.classification.ClassificationResponse;
import com.suryakn.IssueTracker.classification.ClassificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classification")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ClassificationController {

    private final ClassificationService classificationService;

    @PostMapping("/classify")
    public ResponseEntity<ClassificationResponse> classifyTicket(
            @RequestParam String title,
            @RequestParam(required = false) String description) {
        
        ClassificationResponse response = classificationService.classifyTicket(title, description);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        boolean available = classificationService.isClassificationAvailable();
        if (available) {
            return ResponseEntity.ok("Classification service is available");
        } else {
            return ResponseEntity.status(503).body("Classification service is unavailable");
        }
    }
}