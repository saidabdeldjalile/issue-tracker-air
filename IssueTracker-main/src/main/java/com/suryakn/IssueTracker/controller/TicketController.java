package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.dto.AssignRequest;
import com.suryakn.IssueTracker.dto.TicketRequest;
import com.suryakn.IssueTracker.dto.TicketResponse;
import com.suryakn.IssueTracker.dto.TicketUpdateRequest;
import com.suryakn.IssueTracker.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    // Get all tickets for the authenticated user - MUST come before /{id}
    @GetMapping("/my-tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(@RequestParam String email) {
        return ticketService.getTicketsByUserEmail(email);
    }

    // Get tickets based on user role and department - MUST come before /{id}
    @GetMapping("/tickets-for-user")
    public ResponseEntity<List<TicketResponse>> getTicketsForUser(
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam(required = false) Long departmentId) {
        log.info("getTicketsForUser called with email: {}, role: {}, departmentId: {}", email, role, departmentId);
        try {
            return ticketService.getTicketsForUser(email, role, departmentId);
        } catch (Exception e) {
            log.error("Error in getTicketsForUser: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> all() {
        return ticketService.getAllTickets();
    }

    @GetMapping("{id}")
    public ResponseEntity<TicketResponse> ticketWithId(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }


    @PostMapping
    public ResponseEntity<TicketResponse> addTicket(@RequestBody TicketRequest newTicket) {
        return ticketService.addTicket(newTicket);
    }

    @PostMapping("{id}")
    public ResponseEntity<TicketResponse> replaceTicket(@RequestBody TicketRequest ticketRequest, @PathVariable Long id) {
        return ticketService.updateTicket(ticketRequest, id);
    }

    // PATCH endpoint for partial updates (status, priority, etc.)
    @PatchMapping("{id}")
    public ResponseEntity<TicketResponse> updateTicketPartial(@PathVariable Long id, @RequestBody TicketUpdateRequest updateRequest) {
        return ticketService.updateTicketPartial(id, updateRequest);
    }

    @PostMapping("{ticketId}/assign")
    public void assignTicket(@PathVariable Long ticketId, @RequestBody AssignRequest assignRequest) {
        ticketService.assignTicket(ticketId, assignRequest);
    }

    @DeleteMapping("{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllTickets() {
        try {
            ticketService.deleteAllTickets();
            return ResponseEntity.ok("All tickets have been successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting tickets: " + e.getMessage());
        }
    }

    @DeleteMapping("/all/direct")
    public ResponseEntity<String> deleteAllTicketsDirect() {
        try {
            ticketService.deleteAllTicketsDirect();
            return ResponseEntity.ok("All tickets have been successfully deleted (direct SQL approach)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting tickets: " + e.getMessage());
        }
    }
}
