package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.ChatbotFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatbotFeedbackRepository extends JpaRepository<ChatbotFeedback, Long> {

    List<ChatbotFeedback> findBySessionId(String sessionId);

    List<ChatbotFeedback> findByUserEmail(String userEmail);

    @Query("SELECT AVG(f.rating) FROM ChatbotFeedback f WHERE f.createdAt >= :startDate")
    Double getAverageRatingSince(LocalDateTime startDate);

    @Query("SELECT COUNT(f) FROM ChatbotFeedback f WHERE f.helpful = true AND f.createdAt >= :startDate")
    Long countHelpfulFeedbackSince(LocalDateTime startDate);
}