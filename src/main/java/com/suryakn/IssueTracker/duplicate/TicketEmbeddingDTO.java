package com.suryakn.IssueTracker.duplicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DTO for ticket embedding data used in duplicate detection.
 * Includes validation for vector data to prevent data loss.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEmbeddingDTO {

    private static final Pattern VECTOR_PATTERN = Pattern.compile("\\[.*\\]");
    private static final int MIN_VECTOR_LENGTH = 10; // Minimum expected vector string length
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Long ticketId;
    private String vector;

    /**
     * Validates that the vector is properly formatted and not null/empty.
     * Accepts JSON array format like "[0.1, 0.2, ...]" or similar numeric list representations.
     *
     * @return true if the vector is valid, false otherwise
     */
    public boolean isValidVector() {
        if (vector == null || vector.isBlank()) {
            return false;
        }

        String trimmed = vector.trim();

        // Must start with [ and end with ]
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return false;
        }

        // Check minimum length to ensure it's not just "[]"
        if (trimmed.length() < MIN_VECTOR_LENGTH) {
            return false;
        }

        // Try to parse as JSON array to validate structure
        try {
            Object parsed = OBJECT_MAPPER.readValue(trimmed, Object.class);
            if (!(parsed instanceof java.util.List)) {
                return false;
            }
        } catch (JsonProcessingException e) {
            // If JSON parsing fails, try regex-based validation
            if (!VECTOR_PATTERN.matcher(trimmed).matches()) {
                return false;
            }
            // Check that it contains at least some numbers
            String numbersOnly = trimmed.replaceAll("[^\\d.,\\-]", "");
            if (numbersOnly.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates and normalizes the vector string to ensure it's in proper JSON format.
     * This helps prevent data corruption when the vector is sent to the Python service.
     *
     * @return a normalized vector string, or null if the vector is invalid
     */
    public String getNormalizedVector() {
        if (!isValidVector()) {
            return null;
        }

        String trimmed = vector.trim();

        // Try to parse and re-serialize to ensure valid JSON
        try {
            Object parsed = OBJECT_MAPPER.readValue(trimmed, Object.class);
            return OBJECT_MAPPER.writeValueAsString(parsed);
        } catch (JsonProcessingException e) {
            // If JSON parsing fails, return the original trimmed vector
            return trimmed;
        }
    }

    /**
     * Checks if the ticketId is valid (non-null and positive).
     *
     * @return true if the ticketId is valid
     */
    public boolean isValidTicketId() {
        return ticketId != null && ticketId > 0;
    }

    /**
     * Full validation of this DTO - both ticketId and vector must be valid.
     *
     * @return true if this DTO is fully valid
     */
    public boolean isValid() {
        return isValidTicketId() && isValidVector();
    }
}
