package com.tekion.demo.circuitbreaker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Circuit Breaker implementation to prevent cascading failures
 * States: CLOSED (normal) -> OPEN (failing) -> HALF_OPEN (testing) -> CLOSED
 */
public class CircuitBreaker {

    private final int failureThreshold;
    private final Duration timeout;
    private final AtomicInteger failureCount;
    private final AtomicReference<CircuitBreakerState> state;
    private final AtomicReference<Instant> lastFailureTime;

    public CircuitBreaker(int failureThreshold, Duration timeout) {
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
        this.failureCount = new AtomicInteger(0);
        this.state = new AtomicReference<>(CircuitBreakerState.CLOSED);
        this.lastFailureTime = new AtomicReference<>(Instant.now());
    }

    public CircuitBreakerState getState() {
        // Check if we should transition from OPEN to HALF_OPEN
        if (state.get() == CircuitBreakerState.OPEN) {
            if (Duration.between(lastFailureTime.get(), Instant.now()).compareTo(timeout) > 0) {
                state.set(CircuitBreakerState.HALF_OPEN);
                System.out.println("Circuit breaker transitioning to HALF_OPEN");
            }
        }
        return state.get();
    }

    public void recordSuccess() {
        if (state.get() == CircuitBreakerState.HALF_OPEN) {
            // Successful call in HALF_OPEN state, close the circuit
            state.set(CircuitBreakerState.CLOSED);
            failureCount.set(0);
            System.out.println("Circuit breaker CLOSED after successful call");
        } else if (state.get() == CircuitBreakerState.CLOSED) {
            // Reset failure count on success
            failureCount.set(0);
        }
    }

    public void recordFailure() {
        lastFailureTime.set(Instant.now());
        int failures = failureCount.incrementAndGet();

        if (state.get() == CircuitBreakerState.HALF_OPEN) {
            // Failed in HALF_OPEN, go back to OPEN
            state.set(CircuitBreakerState.OPEN);
            System.out.println("Circuit breaker OPEN after failed test call");
        } else if (failures >= failureThreshold && state.get() == CircuitBreakerState.CLOSED) {
            // Too many failures, open the circuit
            state.set(CircuitBreakerState.OPEN);
            System.out.println("Circuit breaker OPEN after " + failures + " failures");
        }
    }

    public boolean allowRequest() {
        CircuitBreakerState currentState = getState();
        
        if (currentState == CircuitBreakerState.OPEN) {
            return false;
        }
        
        // Allow requests in CLOSED and HALF_OPEN states
        return true;
    }

    public void reset() {
        state.set(CircuitBreakerState.CLOSED);
        failureCount.set(0);
        System.out.println("Circuit breaker manually RESET");
    }

    public int getFailureCount() {
        return failureCount.get();
    }
}

