package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.ai.dto.AIChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.suryakn.IssueTracker.util.CircuitBreaker;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIChatClient {

    private final RestTemplate restTemplate;

    @Value("${chatbot.service.url}")
    private String chatbotServiceUrl;

    private final CircuitBreaker circuitBreaker = new CircuitBreaker("ai-chatbot");

    public AIChatResponse chat(String message, String sessionId, String userEmail,
                               Map<String, Object> databaseContext, String authToken) {
        try {
            return circuitBreaker.execute(() -> {
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("message", message);
                if (sessionId != null) {
                    requestMap.put("sessionId", sessionId);
                }
                if (userEmail != null) {
                    requestMap.put("userEmail", userEmail);
                }
                if (databaseContext != null && !databaseContext.isEmpty()) {
                    requestMap.put("databaseContext", databaseContext);
                    log.debug("Sending database context to AI: {} keys", databaseContext.size());
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                if (authToken != null && !authToken.isEmpty()) {
                    headers.set("Authorization", "Bearer " + authToken);
                    log.debug("Propagating auth token to AI service");
                } else {
                    log.warn("No JWT token provided to propagate to AI service");
                }

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);

                String url = chatbotServiceUrl + "/chat";
                log.debug("Calling AI chatbot at: {}", url);
                return restTemplate.postForObject(url, requestEntity, AIChatResponse.class);
            }, () -> {
                log.warn("Circuit breaker fallback: AI service temporarily unavailable");
                return null;
            });
        } catch (Exception e) {
            log.warn("AI chatbot call failed (circuit breaker state: {}): {}", circuitBreaker.getState(), e.getMessage());
            return null;
        }
    }

    public boolean isAvailable() {
        try {
            return circuitBreaker.execute(() -> {
                String result = restTemplate.getForObject(chatbotServiceUrl + "/health", String.class);
                return result != null && result.contains("healthy");
            }, () -> false);
        } catch (Exception e) {
            log.warn("AI chatbot health check failed (circuit breaker state: {}): {}", circuitBreaker.getState(), e.getMessage());
            return false;
        }
    }
}
