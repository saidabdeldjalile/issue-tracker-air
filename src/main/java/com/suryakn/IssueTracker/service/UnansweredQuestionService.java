package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.FAQ;
import com.suryakn.IssueTracker.entity.UnansweredQuestion;
import com.suryakn.IssueTracker.repository.FAQRepository;
import com.suryakn.IssueTracker.repository.UnansweredQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnansweredQuestionService {

    private final UnansweredQuestionRepository unansweredQuestionRepository;
    private final FAQRepository faqRepository;

    @Transactional
    public UnansweredQuestion saveQuestion(String question, String context, String userEmail, 
            String suggestedCategory, String suggestedDepartment, Long relatedTicketId) {
        
        if (unansweredQuestionRepository.existsByQuestionIgnoreCase(question)) {
            log.info("Question already exists in unanswered: {}", question.substring(0, Math.min(50, question.length())));
            return null;
        }

        UnansweredQuestion unanswered = UnansweredQuestion.builder()
                .question(question)
                .context(context)
                .userEmail(userEmail)
                .suggestedCategory(suggestedCategory)
                .suggestedDepartment(suggestedDepartment)
                .relatedTicketId(relatedTicketId)
                .status(UnansweredQuestion.QuestionStatus.PENDING)
                .build();

        return unansweredQuestionRepository.save(unanswered);
    }

    public List<UnansweredQuestion> getPendingQuestions() {
        try {
            return unansweredQuestionRepository.findByStatusOrderByCreatedAtDesc(UnansweredQuestion.QuestionStatus.PENDING);
        } catch (Exception e) {
            log.error("Error fetching pending questions: ", e);
            return List.of(); // Return empty list if table doesn't exist
        }
    }

    public Page<UnansweredQuestion> getPendingQuestions(Pageable pageable) {
        try {
            return unansweredQuestionRepository.findByStatusOrderByCreatedAtDesc(UnansweredQuestion.QuestionStatus.PENDING, pageable);
        } catch (Exception e) {
            log.error("Error fetching pending questions with pagination: ", e);
            return Page.empty(); // Return empty page if table doesn't exist
        }
    }

    public long getPendingCount() {
        try {
            return unansweredQuestionRepository.countByStatus(UnansweredQuestion.QuestionStatus.PENDING);
        } catch (Exception e) {
            log.error("Error counting pending questions: ", e);
            return 0; // Return 0 if table doesn't exist
        }
    }

    @Transactional
    public Optional<UnansweredQuestion> addToFaq(Long id, String answer, String category, String keywords) {
        return unansweredQuestionRepository.findById(id)
                .map(unanswered -> {
                    FAQ faq = FAQ.builder()
                            .question(unanswered.getQuestion())
                            .answer(answer)
                            .category(category)
                            .keywords(keywords != null ? List.of(keywords.split(",")) : null)
                            .active(true)
                            .viewCount(0)
                            .build();

                    FAQ savedFaq = faqRepository.save(faq);

                    unanswered.setStatus(UnansweredQuestion.QuestionStatus.ADDED_TO_FAQ);
                    unanswered.setResolvedAt(LocalDateTime.now());
                    unanswered.setResolvedByFaqId(savedFaq.getId());
                    unansweredQuestionRepository.save(unanswered);

                    log.info("Added question {} to FAQ with id {}", unanswered.getQuestion().substring(0, 30), savedFaq.getId());
                    return unanswered;
                });
    }

    @Transactional
    public Optional<UnansweredQuestion> rejectQuestion(Long id, String reason) {
        return unansweredQuestionRepository.findById(id)
                .map(unanswered -> {
                    unanswered.setStatus(UnansweredQuestion.QuestionStatus.REJECTED);
                    unanswered.setResolvedAt(LocalDateTime.now());
                    unansweredQuestionRepository.save(unanswered);
                    return unanswered;
                });
    }

    @Transactional
    public void markAsReviewed(Long id) {
        unansweredQuestionRepository.findById(id)
                .ifPresent(unanswered -> {
                    unanswered.setStatus(UnansweredQuestion.QuestionStatus.REVIEWED);
                    unansweredQuestionRepository.save(unanswered);
                });
    }

    @Transactional
    public void deleteQuestion(Long id) {
        unansweredQuestionRepository.deleteById(id);
    }
}