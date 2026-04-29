package com.suryakn.IssueTracker.util;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple in-memory circuit breaker to protect AI service calls.
 * State transitions: CLOSED -> OPEN -> HALF_OPEN -> CLOSED
 */
public class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    private static final int FAILURE_THRESHOLD = 5;
    private static final long OPEN_TIMEOUT_MS = 30_000; // 30 seconds
    private static final int HALF_OPEN_MAX_CALLS = 1;

    private final String serviceName;
    private volatile State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private volatile Instant lastFailureTime;
    private volatile int halfOpenCalls = 0;

    public CircuitBreaker(String serviceName) {
        this.serviceName = serviceName;
    }

    public <T> T execute(CallableWithException<T> callable, Fallback<T> fallback) throws Exception {
        if (state == State.OPEN) {
            if (Instant.now().isAfter(lastFailureTime.plusMillis(OPEN_TIMEOUT_MS))) {
                // Transition to HALF_OPEN
                state = State.HALF_OPEN;
                halfOpenCalls = 0;
            } else {
                // Circuit is open, fast-fail
                throw new Exception("Circuit breaker OPEN for service: " + serviceName);
            }
        }

        if (state == State.HALF_OPEN && halfOpenCalls >= HALF_OPEN_MAX_CALLS) {
            throw new Exception("Circuit breaker HALF_OPEN, waiting for probe result");
        }

        try {
            T result = callable.call();
            // Success
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure(e);
            if (fallback != null) {
                return fallback.get();
            }
            throw e;
        }
    }

    private void onSuccess() {
        successCount.incrementAndGet();
        if (state == State.HALF_OPEN) {
            // Successful call in HALF_OPEN resets to CLOSED
            state = State.CLOSED;
        }
        failureCount.set(0); // reset failures on success
    }

    private void onFailure(Exception e) {
        int failures = failureCount.incrementAndGet();
        lastFailureTime = Instant.now();
        if (state == State.CLOSED && failures >= FAILURE_THRESHOLD) {
            state = State.OPEN;
        }
    }

    public State getState() {
        return state;
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public Instant getLastFailureTime() {
        return lastFailureTime;
    }

    @FunctionalInterface
    public interface CallableWithException<T> {
        T call() throws Exception;
    }

    @FunctionalInterface
    public interface Fallback<T> {
        T get();
    }
}
