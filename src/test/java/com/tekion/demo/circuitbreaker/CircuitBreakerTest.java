package com.tekion.demo.circuitbreaker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Circuit Breaker
 */
class CircuitBreakerTest {

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = new CircuitBreaker(3, Duration.ofSeconds(1));
    }

    @Test
    void testInitialStateClosed() {
        assertEquals(CircuitBreakerState.CLOSED, circuitBreaker.getState());
        assertTrue(circuitBreaker.allowRequest());
    }

    @Test
    void testOpenAfterThresholdFailures() {
        // Record 3 failures
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();

        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.getState());
        assertFalse(circuitBreaker.allowRequest());
    }

    @Test
    void testSuccessResetsFailureCount() {
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        assertEquals(2, circuitBreaker.getFailureCount());

        circuitBreaker.recordSuccess();
        assertEquals(0, circuitBreaker.getFailureCount());
        assertEquals(CircuitBreakerState.CLOSED, circuitBreaker.getState());
    }

    @Test
    void testTransitionToHalfOpenAfterTimeout() throws InterruptedException {
        // Open the circuit
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.getState());

        // Wait for timeout
        Thread.sleep(1100);

        // Should transition to HALF_OPEN
        assertEquals(CircuitBreakerState.HALF_OPEN, circuitBreaker.getState());
        assertTrue(circuitBreaker.allowRequest());
    }

    @Test
    void testHalfOpenToClosedOnSuccess() throws InterruptedException {
        // Open the circuit
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();

        // Wait for timeout
        Thread.sleep(1100);
        assertEquals(CircuitBreakerState.HALF_OPEN, circuitBreaker.getState());

        // Success should close the circuit
        circuitBreaker.recordSuccess();
        assertEquals(CircuitBreakerState.CLOSED, circuitBreaker.getState());
    }

    @Test
    void testHalfOpenToOpenOnFailure() throws InterruptedException {
        // Open the circuit
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();

        // Wait for timeout
        Thread.sleep(1100);
        assertEquals(CircuitBreakerState.HALF_OPEN, circuitBreaker.getState());

        // Failure should reopen the circuit
        circuitBreaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.getState());
    }

    @Test
    void testManualReset() {
        // Open the circuit
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();
        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.getState());

        // Manual reset
        circuitBreaker.reset();
        assertEquals(CircuitBreakerState.CLOSED, circuitBreaker.getState());
        assertEquals(0, circuitBreaker.getFailureCount());
    }
}

