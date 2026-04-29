package com.suryakn.IssueTracker.controller;

import com.suryakn.IssueTracker.service.AIChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @PersistenceContext
    private EntityManager entityManager;

    private final AIChatClient aiChatClient;

    public HealthController(AIChatClient aiChatClient) {
        this.aiChatClient = aiChatClient;
    }

    /**
     * Comprehensive health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        boolean databaseOk = checkDatabase();
        boolean aiServiceOk = checkAIService();

        status.put("service", "IssueTracker Backend");
        status.put("status", databaseOk && aiServiceOk ? "UP" : "DEGRADED");
        status.put("database", databaseOk ? "UP" : "DOWN");
        status.put("aiService", aiServiceOk ? "UP" : "DOWN");
        status.put("timestamp", System.currentTimeMillis());

        if (databaseOk && aiServiceOk) {
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.status(503).body(status);
        }
    }

    /**
     * Database connectivity check
     */
    private boolean checkDatabase() {
        try {
            Connection connection = entityManager.unwrap(java.sql.Connection.class);
            if (connection != null && !connection.isClosed()) {
                DatabaseMetaData metaData = connection.getMetaData();
                String productName = metaData.getDatabaseProductName();
                return productName != null && !productName.isBlank();
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * AI service availability check
     */
    private boolean checkAIService() {
        try {
            return aiChatClient.isAvailable();
        } catch (Exception e) {
            return false;
        }
    }
}
