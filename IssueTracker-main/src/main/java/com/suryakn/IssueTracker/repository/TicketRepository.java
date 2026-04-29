package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByProjectId(Long pid);

    Optional<Ticket> findByProjectIdAndId(Long pid, Long tid);

    // Find tickets created by a specific user
    List<Ticket> findByCreatedByEmail(String email);

    // Find tickets assigned to a specific user
    List<Ticket> findByAssignedToEmail(String email);

    // Find tickets where user is either creator or assignee
    List<Ticket> findByCreatedByEmailOrAssignedToEmail(String createdByEmail, String assignedToEmail);

    // Find tickets by project department ID (via project)
    List<Ticket> findByProjectDepartmentId(Long departmentId);

    // Find tickets created by or assigned to users in a specific department
    List<Ticket> findByCreatedByDepartmentIdOrAssignedToDepartmentId(Long createdByDepartmentId, Long assignedToDepartmentId);

    // Find all tickets excluding deleted ones
    List<Ticket> findByStatusNot(com.suryakn.IssueTracker.entity.Status status);

    // Dashboard statistics methods
    @Query("SELECT u.firstName, COUNT(t) FROM Ticket t JOIN t.createdBy u GROUP BY u.firstName ORDER BY COUNT(t) DESC")
    List<Object[]> countTicketsByUser();

    @Query("SELECT u.firstName, COUNT(t) FROM Ticket t JOIN t.createdBy u WHERE t.createdAt BETWEEN :start AND :end GROUP BY u.firstName ORDER BY COUNT(t) DESC")
    List<Object[]> countTicketsByUserAndCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT d.name, COUNT(t) FROM Ticket t JOIN t.project.department d GROUP BY d.name ORDER BY COUNT(t) DESC")
    List<Object[]> countTicketsByDepartment();

    @Query("SELECT d.name, COUNT(t) FROM Ticket t JOIN t.project.department d WHERE t.createdAt BETWEEN :start AND :end GROUP BY d.name ORDER BY COUNT(t) DESC")
    List<Object[]> countTicketsByDepartmentAndCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    //    List<Ticket> findByTitle(String title);

}
