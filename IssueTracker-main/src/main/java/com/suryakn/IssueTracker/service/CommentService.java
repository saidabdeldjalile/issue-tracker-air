package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.CommentDto;
import com.suryakn.IssueTracker.entity.*;
import com.suryakn.IssueTracker.repository.CommentRepository;
import com.suryakn.IssueTracker.repository.TicketRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<CommentDto> commentList(List<Comment> comments) {
        List<CommentDto> commentString = new ArrayList<>();
        for (Comment c : comments) {
            commentString.add(
                    CommentDto.builder()
                            .comment(c.getComment())
                            .email(c.getCreatedBy().getEmail())
                            .username(c.getCreatedBy().getFirstName() + " " + c.getCreatedBy().getLastName())
                            .created(c.getCreatedAt())
                            .role(c.getCreatedBy().getRole() != null ? c.getCreatedBy().getRole().name() : null)
                            .build());
        }
        return commentString;
    }

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
            
            // If commenter is SUPPORT, notify admins, assigned support, and the reporter
            if (commenter.getRole() == Role.SUPPORT) {
                notifyAdminsAndAssignedSupportOnComment(ticket, commenter);
                // Also notify the reporter about the comment
                notifyReporterOnSupportComment(ticket, commenter);
            } else {
                // If commenter is USER (reporter), notify assigned SUPPORT
                notifyAssignedSupportOnUserComment(ticket, commenter);
            }
            
            return new ResponseEntity<>(CommentDto.builder()
                    .comment(savedComment.getComment())
                    .build(), HttpStatus.CREATED);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Send notification to admin users and assigned support when a SUPPORT adds a comment.
     */
    private void notifyAdminsAndAssignedSupportOnComment(Ticket ticket, UserEntity commenter) {
        Department department = ticket.getProject() != null ? ticket.getProject().getDepartment() : null;
        if (department == null) {
            return;
        }
        
        String message = "Nouveau commentaire de " + commenter.getFirstName() + " " + commenter.getLastName() + " sur le ticket '" + ticket.getTitle() + "'";
        
        // Notify all ADMIN users in the department
        List<UserEntity> admins = userRepository.findAllByRoleAndDepartment(Role.ADMIN, department);
        for (UserEntity admin : admins) {
            // Avoid self-notification
            if (!admin.getId().equals(commenter.getId())) {
                notificationService.createNotification(
                    NotificationType.COMMENT_ADDED,
                    "Nouveau commentaire",
                    message,
                    admin,
                    department,
                    ticket.getId()
                );
            }
        }
        
        // Notify assigned SUPPORT (if different from commenter)
        if (ticket.getAssignedTo() != null && 
            ticket.getAssignedTo().getRole() == Role.SUPPORT &&
            !ticket.getAssignedTo().getId().equals(commenter.getId())) {
            notificationService.createNotification(
                NotificationType.COMMENT_ADDED,
                "Nouveau commentaire",
                message,
                ticket.getAssignedTo(),
                department,
                ticket.getId()
            );
        }
        
        log.info("Notifications sent for comment on ticket {} by SUPPORT {}", ticket.getId(), commenter.getEmail());
    }
    
    /**
     * Send notification to the ticket reporter (creator) when SUPPORT adds a comment.
     */
    private void notifyReporterOnSupportComment(Ticket ticket, UserEntity commenter) {
        // Don't notify if there's no reporter
        if (ticket.getCreatedBy() == null) {
            return;
        }
        
        // Don't notify if the commenter is the reporter themselves
        if (ticket.getCreatedBy().getId().equals(commenter.getId())) {
            return;
        }
        
        Department department = ticket.getProject() != null ? ticket.getProject().getDepartment() : null;
        
        String message = "Le support a ajouté un commentaire sur votre ticket '" + ticket.getTitle() + "'";
        
        notificationService.createNotification(
            NotificationType.COMMENT_ADDED,
            "Nouveau commentaire sur votre ticket",
            message,
            ticket.getCreatedBy(),
            department,
            ticket.getId()
        );
        
        log.info("Notification sent to reporter {} about comment on ticket {} by SUPPORT {}", 
            ticket.getCreatedBy().getEmail(), ticket.getId(), commenter.getEmail());
    }
    
    /**
     * Send notification to assigned SUPPORT when a USER adds a comment.
     */
    private void notifyAssignedSupportOnUserComment(Ticket ticket, UserEntity commenter) {
        // Don't notify if there's no assigned support
        if (ticket.getAssignedTo() == null) {
            return;
        }
        
        // Don't notify if the commenter is the assigned support themselves
        if (ticket.getAssignedTo().getId().equals(commenter.getId())) {
            return;
        }
        
        // Only notify if the assigned user is SUPPORT
        if (ticket.getAssignedTo().getRole() != Role.SUPPORT) {
            return;
        }
        
        Department department = ticket.getProject() != null ? ticket.getProject().getDepartment() : null;
        
        String message = commenter.getFirstName() + " " + commenter.getLastName() + " a ajouté un commentaire sur le ticket '" + ticket.getTitle() + "'";
        
        notificationService.createNotification(
            NotificationType.COMMENT_ADDED,
            "Nouveau commentaire d'un utilisateur",
            message,
            ticket.getAssignedTo(),
            department,
            ticket.getId()
        );
        
        log.info("Notification sent to assigned support {} about comment on ticket {} by USER {}", 
            ticket.getAssignedTo().getEmail(), ticket.getId(), commenter.getEmail());
    }

    public ResponseEntity<List<CommentDto>> getAllComments(Long ticketId) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {

            return new ResponseEntity<>(commentList(commentRepository.findByTicket_Id(ticketId)), HttpStatus.OK);
        }
    }
    
    public List<Comment> getOrphanedComments() {
        // Get all comments for tickets that are marked as Deleted
        return commentRepository.findByTicket_Status(Status.Deleted);
    }

    public void deleteComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        optionalComment.ifPresent(commentRepository::delete);
        log.info("Comment {} deleted successfully", id);
    }
}
