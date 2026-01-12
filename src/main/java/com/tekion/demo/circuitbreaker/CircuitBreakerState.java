package com.tekion.demo.circuitbreaker;

/**
 * Circuit Breaker States
 */
public enum CircuitBreakerState {
    /**
     * Normal operation - requests are allowed
     */
    CLOSED,
    
    /**
     * Too many failures - requests are blocked
     */
    OPEN,
    
    /**
     * Testing if service recovered - limited requests allowed
     */
    HALF_OPEN
}

