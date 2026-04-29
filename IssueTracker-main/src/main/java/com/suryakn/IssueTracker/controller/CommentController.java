package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.CommentDto;
import com.suryakn.IssueTracker.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/tickets/{ticketId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> all(@PathVariable Long ticketId) {
        return commentService.getAllComments(ticketId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<CommentDto> saveComment(@PathVariable Long ticketId, @RequestBody CommentDto body) {
        return commentService.addComment(ticketId, body);
    }

//    @DeleteMapping
//    TODO: delete individual comment
//    public void deleteComment(@PathVariable Long ticketId) {
//        commentRepository.
//    }
}
