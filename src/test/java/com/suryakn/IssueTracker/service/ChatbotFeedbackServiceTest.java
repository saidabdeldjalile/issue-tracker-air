package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.dto.ChatbotFeedbackDto;
import com.suryakn.IssueTracker.entity.ChatbotFeedback;
import com.suryakn.IssueTracker.repository.ChatbotFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotFeedbackServiceTest {

    @Mock
    private ChatbotFeedbackRepository feedbackRepository;

    @InjectMocks
    private ChatbotFeedbackService feedbackService;

    private ChatbotFeedbackDto feedbackDto;
    private ChatbotFeedback feedback;

    @BeforeEach
    void setUp() {
        feedbackDto = ChatbotFeedbackDto.builder()
                .sessionId("session123")
                .userEmail("user@airalgerie.dz")
                .message("Test message")
                .response("Test response")
                .rating(5)
                .helpful(true)
                .build();

        feedback = ChatbotFeedback.builder()
                .id(1L)
                .sessionId("session123")
                .userEmail("user@airalgerie.dz")
                .message("Test message")
                .response("Test response")
                .rating(5)
                .helpful(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void saveFeedback_ShouldReturnSavedFeedbackDto() {
        when(feedbackRepository.save(any(ChatbotFeedback.class))).thenReturn(feedback);

        ChatbotFeedbackDto result = feedbackService.saveFeedback(feedbackDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSessionId()).isEqualTo("session123");
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getHelpful()).isTrue();
    }

    @Test
    void getFeedbackBySession_ShouldReturnFeedbackList() {
        when(feedbackRepository.findBySessionId("session123")).thenReturn(Arrays.asList(feedback));

        List<ChatbotFeedbackDto> result = feedbackService.getFeedbackBySession("session123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSessionId()).isEqualTo("session123");
    }

    @Test
    void getFeedbackByUser_ShouldReturnFeedbackList() {
        when(feedbackRepository.findByUserEmail("user@airalgerie.dz")).thenReturn(Arrays.asList(feedback));

        List<ChatbotFeedbackDto> result = feedbackService.getFeedbackByUser("user@airalgerie.dz");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserEmail()).isEqualTo("user@airalgerie.dz");
    }

    @Test
    void getAverageRatingLast30Days_ShouldReturnAverage() {
        when(feedbackRepository.getAverageRatingSince(any(LocalDateTime.class))).thenReturn(4.5);

        Double result = feedbackService.getAverageRatingLast30Days();

        assertThat(result).isEqualTo(4.5);
    }

    @Test
    void getHelpfulFeedbackCountLast30Days_ShouldReturnCount() {
        when(feedbackRepository.countHelpfulFeedbackSince(any(LocalDateTime.class))).thenReturn(10L);

        Long result = feedbackService.getHelpfulFeedbackCountLast30Days();

        assertThat(result).isEqualTo(10L);
    }
}