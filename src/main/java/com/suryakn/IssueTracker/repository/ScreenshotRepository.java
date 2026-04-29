package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
    List<Screenshot> findByTicketId(Long ticketId);
}