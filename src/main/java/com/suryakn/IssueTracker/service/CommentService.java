package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.CommentDto;
import com.suryakn.IssueTracker.entity.*;
import com.suryakn.IssueTracker.repository.CommentRepository;
import com.suryakn.IssueTracker.repository.CommentScreenshotRepository;
import com.suryakn.IssueTracker.repository.TicketRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentScreenshotRepository commentScreenshotRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    // private final AttachmentRepository attachmentRepository;

    @Value("${app.upload.path:uploads/screenshots}")
    private String uploadPath;

    public List<CommentDto> commentList(List<Comment> comments) {
        List<CommentDto> commentString = new ArrayList<>();
        for (Comment c : comments) {
            commentString.add(
                    CommentDto.builder()
                            .id(c.getId())
                            .comment(c.getComment())
                            .email(c.getCreatedBy().getEmail())
                            .username(c.getCreatedBy().getFirstName() + " " + c.getCreatedBy().getLastName())
                            .created(c.getCreatedAt())
                            .role(c.getCreatedBy().getRole() != null ? c.getCreatedBy().getRole().name() : null)
                            .build());
        }
        return commentString;
    }
    
    // mapAttachmentToDto removed - attachments functionality removed

    public ResponseEntity<CommentDto> addComment(Long ticketId, CommentDto body) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);

        return optionalTicket.map(ticket -> {
            UserEntity commenter = userRepository.findByEmail(body.getEmail()).orElseThrow();
            
            Comment comment = Comment.builder()
                    .comment(body.getComment())
                    .ticket(ticket)
                    .createdBy(commenter)
                    .build();
            
            Comment savedComment = commentRepository.save(comment);
            
            // Notify based on commenter identity (createur vs assignee), not role
            boolean isCreator = ticket.getCreatedBy() != null && ticket.getCreatedBy().getId().equals(commenter.getId());
            boolean isAssignee = ticket.getAssignedTo() != null && ticket.getAssignedTo().getId().equals(commenter.getId());
            
            if (isCreator) {
                // Creator added comment → notify assignee
                notifyAssigneeOnCreatorComment(ticket, commenter);
            } else if (isAssignee) {
                // Assignee added comment → notify creator
                notifyCreatorOnAssigneeComment(ticket, commenter);
            } else {
                // Neither creator nor assignee (e.g., another admin) → notify both
                notifyBothOnComment(ticket, commenter);
            }
            
            return new ResponseEntity<>(CommentDto.builder()
                    .id(savedComment.getId())
                    .comment(savedComment.getComment())
                    .build(), HttpStatus.CREATED);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Notify assignee when the ticket creator adds a comment.
     */
    private void notifyAssigneeOnCreatorComment(Ticket ticket, UserEntity commenter) {
        if (ticket.getAssignedTo() == null) {
            return;
        }
        
        Department department = ticket.getProject() != null ? ticket.getProject().getDepartment() : null;
        
        String message = commenter.getFirstName() + " " + commenter.getLastName() + " a ajouté un commentaire sur le ticket '" + ticket.getTitle() + "'";
        
        notificationService.createNotification(
            NotificationType.COMMENT_ADDED,
            "Nouveau commentaire",
            message,
            ticket.getAssignedTo(),
            department,
            ticket.getId()
        );
        
        log.info("Notification sent to assignee {} about comment on ticket {} by creator {}", 
            ticket.getAssignedTo().getEmail(), ticket.getId(), commenter.getEmail());
    }
    
    /**
     * Notify creator when the assignee adds a comment.
     */
    private void notifyCreatorOnAssigneeComment(Ticket ticket, UserEntity commenter) {
        if (ticket.getCreatedBy() == null) {
            return;
        }
        
        Department department = ticket.getProject() != null ? ticket.getProject().getDepartment() : null;
        
        String message = "Nouveau commentaire de " + commenter.getFirstName() + " " + commenter.getLastName() + " sur votre ticket '" + ticket.getTitle() + "'";
        
        notificationService.createNotification(
            NotificationType.COMMENT_ADDED,
            "Nouveau commentaire sur votre ticket",
            message,
            ticket.getCreatedBy(),
            department,
            ticket.getId()
        );
        
        log.info("Notification sent to creator {} about comment on ticket {} by assignee {}", 
            ticket.getCreatedBy().getEmail(), ticket.getId(), commenter.getEmail());
    }
    
    /**
     * Notify both creator and assignee when neither is the commenter.
     */
    private void notifyBothOnComment(Ticket ticket, UserEntity commenter) {
        Department department = ticket.getProject() != null ? ticket.getProject().getDepartment() : null;
        String message = "Nouveau commentaire de " + commenter.getFirstName() + " " + commenter.getLastName() + " sur le ticket '" + ticket.getTitle() + "'";
        
        // Notify creator
        if (ticket.getCreatedBy() != null && !ticket.getCreatedBy().getId().equals(commenter.getId())) {
            notificationService.createNotification(
                NotificationType.COMMENT_ADDED,
                "Nouveau commentaire",
                message,
                ticket.getCreatedBy(),
                department,
                ticket.getId()
            );
        }
        
        // Notify assignee
        if (ticket.getAssignedTo() != null && !ticket.getAssignedTo().getId().equals(commenter.getId())) {
            notificationService.createNotification(
                NotificationType.COMMENT_ADDED,
                "Nouveau commentaire",
                message,
                ticket.getAssignedTo(),
                department,
                ticket.getId()
            );
        }
        
        log.info("Notifications sent to creator and assignee about comment on ticket {} by {}", 
            ticket.getId(), commenter.getEmail());
    }

    public ResponseEntity<List<CommentDto>> getAllComments(Long ticketId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {

            return new ResponseEntity<>(commentList(commentRepository.findByTicket_Id(ticketId)), HttpStatus.OK);
        }
    }
    
    public ResponseEntity<String> deleteComment(Long id, String requesterEmail) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            log.warn("Comment {} not found for deletion", id);
            return new ResponseEntity<>("Comment not found", HttpStatus.NOT_FOUND);
        }

        Comment comment = optionalComment.get();

        // Check authorization: allow deletion if user is the comment author or has ADMIN/SUPPORT role
        String commentAuthorEmail = comment.getCreatedBy().getEmail();
        boolean isAuthor = commentAuthorEmail != null && commentAuthorEmail.equals(requesterEmail);
        boolean isAdminOrSupport = false;
        
        try {
            UserEntity commenter = userRepository.findByEmail(requesterEmail).orElse(null);
            if (commenter != null) {
                Role role = commenter.getRole();
                isAdminOrSupport = (role == Role.ADMIN || role == Role.SUPPORT);
            }
        } catch (Exception e) {
            log.warn("Could not determine role for user {}: {}", requesterEmail, e.getMessage());
        }

        if (!isAuthor && !isAdminOrSupport) {
            log.warn("User {} is not authorized to delete comment {}", requesterEmail, id);
            return new ResponseEntity<>("Forbidden: You can only delete your own comments", HttpStatus.FORBIDDEN);
        }

        // First, find and delete all associated comment screenshots (files + DB records)
        List<CommentScreenshot> commentScreenshots = commentScreenshotRepository.findByCommentId(id);
        for (CommentScreenshot screenshot : commentScreenshots) {
            try {
                // Delete physical file
                String imageUrl = screenshot.getImageUrl();
                if (imageUrl != null && imageUrl.startsWith("/api/screenshots/")) {
                    String fileName = imageUrl.substring("/api/screenshots/".length());
                    Path filePath = Paths.get(uploadPath).resolve(fileName);
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        log.info("Deleted screenshot file: {}", fileName);
                    }
                }
                // Delete DB record (cascade will handle this, but we delete explicitly to ensure file cleanup)
                commentScreenshotRepository.delete(screenshot);
            } catch (IOException e) {
                log.error("Error deleting screenshot file for comment {}: {}", id, e.getMessage());
                // Continue with deletion even if file delete fails
            }
        }

        try {
            commentRepository.delete(comment);
            log.info("Comment {} deleted successfully by user {}", id, requesterEmail);
            return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", id, e.getMessage());
            return new ResponseEntity<>("Error deleting comment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
