package com.suryakn.IssueTracker.classification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassificationService {

    private final RestTemplate restTemplate;

    @Value("${classification.service.url}")
    private String classificationServiceUrl;

    public ClassificationResponse classifyTicket(String title, String description) {
        try {
            ClassificationRequest request = ClassificationRequest.builder()
                    .title(title != null ? title : "")
                    .text(description != null ? description : "")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ClassificationRequest> httpRequest = new HttpEntity<>(request, headers);

            String url = classificationServiceUrl + "/classify";
            log.debug("Calling classification service at: {}", url);

            ClassificationResponse response = restTemplate.postForObject(url, httpRequest, ClassificationResponse.class);

            if (response == null) {
                log.warn("Classification service returned null response");
                return getDefaultResponse();
            }

            log.info("Classification completed: category={}, department={}, priority={}",
                    response.getCategory(), response.getSuggestedDepartment(), response.getSuggestedPriority());

            return response;

        } catch (RestClientException e) {
            log.error("Failed to connect to classification service: {}", e.getMessage());
            return getDefaultResponse();
        } catch (Exception e) {
            log.error("Unexpected error in classification: {}", e.getMessage(), e);
            return getDefaultResponse();
        }
    }

    private ClassificationResponse getDefaultResponse() {
        return ClassificationResponse.builder()
                .category("autres")
                .suggestedDepartment("Support")
                .suggestedPriority("Medium")
                .keywords(java.util.Collections.emptyList())
                .confidence(0.0)
                .build();
    }

    public boolean isClassificationAvailable() {
        try {
            restTemplate.getForObject(classificationServiceUrl + "/health", String.class);
            return true;
        } catch (Exception e) {
            log.warn("Classification service is not available: {}", e.getMessage());
            return false;
        }
    }
}