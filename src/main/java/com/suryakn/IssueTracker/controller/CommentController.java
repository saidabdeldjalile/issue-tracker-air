package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.CommentDto;
import com.suryakn.IssueTracker.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> all(@PathVariable Long ticketId) {
        return commentService.getAllComments(ticketId);
    }

    @PostMapping
    // Allow all authenticated users (ADMIN, SUPPORT, USER) to add comments
    public ResponseEntity<CommentDto> saveComment(@PathVariable Long ticketId, @Valid @RequestBody CommentDto body) {
        return commentService.addComment(ticketId, body);
    }

    @DeleteMapping("{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            Authentication authentication) {
        String email = authentication.getName();
        return commentService.deleteComment(commentId, email);
    }
}
