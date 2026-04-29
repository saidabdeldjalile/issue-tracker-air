package com.suryakn.IssueTracker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "User not found");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid credentials");
        error.put("message", "Invalid email or password");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid request");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid parameter type");
        error.put("message", "Invalid value for parameter: " + ex.getName());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error("No handler found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Endpoint not found");
        error.put("message", "The requested endpoint does not exist");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        log.error("Database constraint violation: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid data");
        error.put("message", "Foreign key violation: Invalid project ID, user email, or department");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        
        String message = ex.getMessage();
        if (message == null) {
            message = "An unexpected error occurred";
        }
        
        if (ex instanceof java.lang.InterruptedException || 
            message.contains("Interrupted") ||
            message.contains("connection acquisition")) {
            error.put("error", "Service temporarily unavailable");
            message = "Please try again. If the problem persists, please contact support.";
            log.warn("Connection pool issue detected - may need to increase pool size");
        } else if (message.length() > 100) {
            message = message.substring(0, 100) + "...";
        }
        
        error.put("message", message);
        error.put("type", ex.getClass().getSimpleName());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof org.hibernate.exception.GenericJDBCException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        
        return new ResponseEntity<>(error, status);
    }
}

