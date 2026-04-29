package com.suryakn.IssueTracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryakn.IssueTracker.dto.ChatRequest;
import com.suryakn.IssueTracker.dto.ChatResponse;
import com.suryakn.IssueTracker.dto.ChatbotFeedbackDto;
import com.suryakn.IssueTracker.service.ChatService;
import com.suryakn.IssueTracker.service.ChatbotFeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@WithMockUser
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ChatbotFeedbackService feedbackService;

    private ChatRequest chatRequest;
    private ChatResponse chatResponse;
    private ChatbotFeedbackDto feedbackDto;

    @BeforeEach
    void setUp() {
        chatRequest = ChatRequest.builder()
                .message("Test message")
                .userEmail("user@airalgerie.dz")
                .sessionId("session123")
                .build();

        chatResponse = ChatResponse.builder()
                .response("Test response")
                .intent("greet")
                .category("autres")
                .build();

        feedbackDto = ChatbotFeedbackDto.builder()
                .id(1L)
                .sessionId("session123")
                .userEmail("user@airalgerie.dz")
                .message("Test message")
                .response("Test response")
                .rating(5)
                .helpful(true)
                .build();
    }

    @Test
    void processChat_ShouldReturnChatResponse() throws Exception {
        when(chatService.processChat(any(ChatRequest.class), any(String.class))).thenReturn(chatResponse);

        mockMvc.perform(post("/api/v1/chat")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Test response"))
                .andExpect(jsonPath("$.intent").value("greet"));
    }

    @Test
    void submitFeedback_ShouldReturnSavedFeedback() throws Exception {
        when(feedbackService.saveFeedback(any(ChatbotFeedbackDto.class))).thenReturn(feedbackDto);

        mockMvc.perform(post("/api/v1/chat/feedback")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getFeedbackBySession_ShouldReturnFeedbackList() throws Exception {
        List<ChatbotFeedbackDto> feedbackList = Arrays.asList(feedbackDto);
        when(feedbackService.getFeedbackBySession("session123")).thenReturn(feedbackList);

        mockMvc.perform(get("/api/v1/chat/feedback/session/session123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value("session123"));
    }

    @Test
    void getFeedbackByUser_ShouldReturnFeedbackList() throws Exception {
        List<ChatbotFeedbackDto> feedbackList = Arrays.asList(feedbackDto);
        when(feedbackService.getFeedbackByUser("user@airalgerie.dz")).thenReturn(feedbackList);

        mockMvc.perform(get("/api/v1/chat/feedback/user/user@airalgerie.dz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userEmail").value("user@airalgerie.dz"));
    }

    @Test
    void getFeedbackStats_ShouldReturnStats() throws Exception {
        Map<String, Object> stats = Map.of(
                "averageRating", 4.5,
                "helpfulFeedbackCount", 10L
        );
        when(feedbackService.getAverageRatingLast30Days()).thenReturn(4.5);
        when(feedbackService.getHelpfulFeedbackCountLast30Days()).thenReturn(10L);

        mockMvc.perform(get("/api/v1/chat/feedback/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.helpfulFeedbackCount").value(10));
    }

    @Test
    void processChat_WithInvalidMessage_ShouldReturnBadRequest() throws Exception {
        ChatRequest invalidRequest = ChatRequest.builder()
                .message("") // Empty message
                .userEmail("user@airalgerie.dz")
                .build();

        mockMvc.perform(post("/api/v1/chat")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}