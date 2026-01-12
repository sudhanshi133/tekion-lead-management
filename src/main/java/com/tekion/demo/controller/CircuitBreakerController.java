package com.tekion.demo.controller;

import com.tekion.demo.adapter.EmailNotificationAdapter;
import com.tekion.demo.adapter.SmsNotificationAdapter;
import com.tekion.demo.circuitbreaker.CircuitBreakerState;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for circuit breaker management
 */
@RestController
@RequestMapping("/api/circuit-breaker")
public class CircuitBreakerController {

    private final EmailNotificationAdapter emailAdapter;
    private final SmsNotificationAdapter smsAdapter;

    public CircuitBreakerController(EmailNotificationAdapter emailAdapter, SmsNotificationAdapter smsAdapter) {
        this.emailAdapter = emailAdapter;
        this.smsAdapter = smsAdapter;
    }

    /**
     * Get circuit breaker status for all adapters
     * GET /api/circuit-breaker/status
     */
    @GetMapping("/status")
    public Map<String, CircuitBreakerState> getStatus() {
        Map<String, CircuitBreakerState> status = new HashMap<>();
        status.put("email", emailAdapter.getCircuitBreakerState());
        status.put("sms", smsAdapter.getCircuitBreakerState());
        return status;
    }

    /**
     * Reset email circuit breaker
     * POST /api/circuit-breaker/reset/email
     */
    @PostMapping("/reset/email")
    public Map<String, String> resetEmailCircuitBreaker() {
        emailAdapter.resetCircuitBreaker();
        return Map.of("message", "Email circuit breaker reset", "state", emailAdapter.getCircuitBreakerState().toString());
    }

    /**
     * Reset SMS circuit breaker
     * POST /api/circuit-breaker/reset/sms
     */
    @PostMapping("/reset/sms")
    public Map<String, String> resetSmsCircuitBreaker() {
        smsAdapter.resetCircuitBreaker();
        return Map.of("message", "SMS circuit breaker reset", "state", smsAdapter.getCircuitBreakerState().toString());
    }
}

