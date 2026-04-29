package com.suryakn.IssueTracker.repository;

import com.suryakn.IssueTracker.entity.UnansweredQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnansweredQuestionRepository extends JpaRepository<UnansweredQuestion, Long> {

    List<UnansweredQuestion> findByStatusOrderByCreatedAtDesc(UnansweredQuestion.QuestionStatus status);

    Page<UnansweredQuestion> findByStatusOrderByCreatedAtDesc(UnansweredQuestion.QuestionStatus status, Pageable pageable);

    long countByStatus(UnansweredQuestion.QuestionStatus status);

    boolean existsByQuestionIgnoreCase(String question);
}