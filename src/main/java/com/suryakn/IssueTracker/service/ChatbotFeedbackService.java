package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.ChatbotFeedbackDto;
import com.suryakn.IssueTracker.entity.ChatbotFeedback;
import com.suryakn.IssueTracker.repository.ChatbotFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotFeedbackService {

    private final ChatbotFeedbackRepository feedbackRepository;

    @Transactional
    public ChatbotFeedbackDto saveFeedback(ChatbotFeedbackDto feedbackDto) {
        ChatbotFeedback feedback = ChatbotFeedback.builder()
                .sessionId(feedbackDto.getSessionId())
                .userEmail(feedbackDto.getUserEmail())
                .message(feedbackDto.getMessage())
                .response(feedbackDto.getResponse())
                .rating(feedbackDto.getRating())
                .feedback(feedbackDto.getFeedback())
                .helpful(feedbackDto.getHelpful())
                .createdAt(LocalDateTime.now())
                .build();

        ChatbotFeedback saved = feedbackRepository.save(feedback);
        log.info("Saved chatbot feedback for session: {}", feedbackDto.getSessionId());

        return convertToDto(saved);
    }

    public List<ChatbotFeedbackDto> getFeedbackBySession(String sessionId) {
        return feedbackRepository.findBySessionId(sessionId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ChatbotFeedbackDto> getFeedbackByUser(String userEmail) {
        return feedbackRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Double getAverageRatingLast30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return feedbackRepository.getAverageRatingSince(thirtyDaysAgo);
    }

    public Long getHelpfulFeedbackCountLast30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return feedbackRepository.countHelpfulFeedbackSince(thirtyDaysAgo);
    }

    private ChatbotFeedbackDto convertToDto(ChatbotFeedback feedback) {
        return ChatbotFeedbackDto.builder()
                .id(feedback.getId())
                .sessionId(feedback.getSessionId())
                .userEmail(feedback.getUserEmail())
                .message(feedback.getMessage())
                .response(feedback.getResponse())
                .rating(feedback.getRating())
                .feedback(feedback.getFeedback())
                .helpful(feedback.getHelpful())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}