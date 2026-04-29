package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.ChatRequest;
import com.suryakn.IssueTracker.dto.ChatResponse;
import com.suryakn.IssueTracker.dto.ChatbotFeedbackDto;
import com.suryakn.IssueTracker.entity.FAQ;
import com.suryakn.IssueTracker.service.ChatService;
import com.suryakn.IssueTracker.service.ChatbotFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:5174", "http://127.0.0.1:5174"})
public class ChatController {

    private final ChatService chatService;
    private final ChatbotFeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ChatResponse> processChat(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        log.info("Chat request received: {}", request.getMessage());
        log.debug("Authorization header: {}", (authorizationHeader != null ? "Bearer <token>" : "null"));

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ChatResponse.builder()
                            .response("Le message ne peut pas être vide.")
                            .build());
        }

        // Extract JWT token from Authorization header
        String authToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authToken = authorizationHeader.substring(7);
            log.debug("Extracted JWT token (length: {})", authToken.length());
        } else {
            log.warn("No Authorization header or not Bearer format");
        }

        ChatResponse response = chatService.processChat(request, authToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }

    @PostMapping("/feedback")
    public ResponseEntity<ChatbotFeedbackDto> submitFeedback(@Valid @RequestBody ChatbotFeedbackDto feedback) {
        log.info("Chatbot feedback received from user: {}", feedback.getUserEmail());
        ChatbotFeedbackDto saved = feedbackService.saveFeedback(feedback);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/feedback/session/{sessionId}")
    public ResponseEntity<List<ChatbotFeedbackDto>> getFeedbackBySession(@PathVariable String sessionId) {
        List<ChatbotFeedbackDto> feedback = feedbackService.getFeedbackBySession(sessionId);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/feedback/user/{userEmail}")
    public ResponseEntity<List<ChatbotFeedbackDto>> getFeedbackByUser(@PathVariable String userEmail) {
        List<ChatbotFeedbackDto> feedback = feedbackService.getFeedbackByUser(userEmail);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/feedback/stats")
    public ResponseEntity<Map<String, Object>> getFeedbackStats() {
        Double averageRating = feedbackService.getAverageRatingLast30Days();
        Long helpfulCount = feedbackService.getHelpfulFeedbackCountLast30Days();

        Map<String, Object> stats = Map.of(
                "averageRating", averageRating != null ? averageRating : 0.0,
                "helpfulFeedbackCount", helpfulCount != null ? helpfulCount : 0L
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FAQ>> searchKnowledgeBase(@RequestParam String query) {
        List<FAQ> results = chatService.searchKnowledgeBase(query);
        return ResponseEntity.ok(results);
    }
}
