package com.suryakn.IssueTracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String correlationId = getOrGenerateCorrelationId(request);
        
        try {
            // Put correlation ID into MDC for structured logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            
            // Add correlation ID to response headers
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Log incoming request with correlation ID
            log.debug("Incoming request: {} {} (correlationId={})", 
                request.getMethod(), request.getRequestURI(), correlationId);
            
            filterChain.doFilter(request, response);
        } finally {
            // Always clear MDC to prevent memory leaks
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    private String getOrGenerateCorrelationId(HttpServletRequest request) {
        // Check if correlation ID already exists in request header (propagated from upstream)
        String existingId = request.getHeader(CORRELATION_ID_HEADER);
        if (existingId != null && !existingId.isBlank()) {
            return existingId;
        }
        
        // Generate new correlation ID
        return UUID.randomUUID().toString();
    }
}
