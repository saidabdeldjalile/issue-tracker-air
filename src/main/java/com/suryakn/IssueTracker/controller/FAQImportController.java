package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.service.FAQImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FAQImportController {

    private final FAQImportService faqImportService;

    @PostMapping("/import-faq-from-tickets")
    public ResponseEntity<Map<String, Object>> importFAQFromTickets() {
        int imported = faqImportService.importSolutionsFromTickets();
        int totalTickets = faqImportService.getResolvedTicketsCount();
        int totalFAQ = faqImportService.getFAQCount();
        
        return ResponseEntity.ok(Map.of(
            "imported", imported,
            "totalResolvedTickets", totalTickets,
            "totalFAQ", totalFAQ,
            "message", "FAQ importée depuis les tickets résolus"
        ));
    }

    @GetMapping("/faq-stats")
    public ResponseEntity<Map<String, Object>> getFAQStats() {
        return ResponseEntity.ok(Map.of(
            "resolvedTickets", faqImportService.getResolvedTicketsCount(),
            "faqCount", faqImportService.getFAQCount()
        ));
    }
}