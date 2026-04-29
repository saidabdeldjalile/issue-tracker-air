package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.*;
import com.suryakn.IssueTracker.repository.TicketRepository;
import com.suryakn.IssueTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceRoleFilterTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    private UserEntity adminUser;
    private UserEntity supportUser;
    private UserEntity regularUser;
    private Project project;
    private List<Ticket> allTickets;

    @BeforeEach
    void setUp() {
        // Create test users
        adminUser = UserEntity.builder()
                .id(1)
                .email("admin@example.com")
                .role(Role.ADMIN)
                .firstName("Admin")
                .lastName("User")
                .build();

        supportUser = UserEntity.builder()
                .id(2)
                .email("support@example.com")
                .role(Role.SUPPORT)
                .firstName("Support")
                .lastName("User")
                .build();

        regularUser = UserEntity.builder()
                .id(3)
                .email("user@example.com")
                .role(Role.USER)
                .firstName("Regular")
                .lastName("User")
                .build();

        // Create test project
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .build();

        // Create test tickets
        Ticket ticket1 = Ticket.builder()
                .id(1L)
                .title("Ticket 1")
                .project(project)
                .createdBy(adminUser)
                .assignedTo(supportUser)
                .build();

        Ticket ticket2 = Ticket.builder()
                .id(2L)
                .title("Ticket 2")
                .project(project)
                .createdBy(supportUser)
                .assignedTo(null)
                .build();

        Ticket ticket3 = Ticket.builder()
                .id(3L)
                .title("Ticket 3")
                .project(project)
                .createdBy(regularUser)
                .assignedTo(supportUser)
                .build();

        Ticket ticket4 = Ticket.builder()
                .id(4L)
                .title("Ticket 4")
                .project(project)
                .createdBy(adminUser)
                .assignedTo(regularUser)
                .build();

        allTickets = Arrays.asList(ticket1, ticket2, ticket3, ticket4);
    }

    @Test
    void testAdminSeesAllTickets() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(ticketRepository.findAllByProjectId(eq(1L)))
                .thenReturn(allTickets);

        // Act
        var result = ticketService.getAllTicketByProjectIdForUser(
                1L, "admin@example.com", "ADMIN", null, pageable, null);

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Page<?> tickets = result.getBody();
        assertNotNull(tickets);
        assertEquals(4, tickets.getTotalElements());
    }

    @Test
    void testSupportSeesOnlyAssignedOrCreatedByTickets() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(ticketRepository.findAllByProjectId(eq(1L)))
                .thenReturn(allTickets);

        // Act
        var result = ticketService.getAllTicketByProjectIdForUser(
                1L, "support@example.com", "SUPPORT", null, pageable, null);

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Page<?> tickets = result.getBody();
        assertNotNull(tickets);
        // Support should see:
        // - Ticket 1 (assigned to support)
        // - Ticket 2 (created by support)
        // - Ticket 3 (assigned to support)
        // Total: 3 tickets
        assertEquals(3, tickets.getTotalElements());
    }

    @Test
    void testRegularUserSeesOnlyAssignedOrCreatedByTickets() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(ticketRepository.findAllByProjectId(eq(1L)))
                .thenReturn(allTickets);

        // Act
        var result = ticketService.getAllTicketByProjectIdForUser(
                1L, "user@example.com", "USER", null, pageable, null);

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Page<?> tickets = result.getBody();
        assertNotNull(tickets);
        // Regular user should see:
        // - Ticket 3 (created by user)
        // - Ticket 4 (assigned to user)
        // Total: 2 tickets
        assertEquals(2, tickets.getTotalElements());
    }

    @Test
    void testUserWithNoTickets() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(ticketRepository.findAllByProjectId(eq(1L)))
                .thenReturn(allTickets);

        // Act
        var result = ticketService.getAllTicketByProjectIdForUser(
                1L, "nonexistent@example.com", "USER", null, pageable, null);

        // Assert
        assertTrue(result.getStatusCode().is2xxSuccessful());
        Page<?> tickets = result.getBody();
        assertNotNull(tickets);
        assertEquals(0, tickets.getTotalElements());
    }
}