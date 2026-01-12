package com.tekion.demo.audit;

import com.tekion.demo.lead.LeadState;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;

/**
 * Audit entry for tracking state transitions
 */
@Value
@Builder
public class AuditEntry {
    String leadId;
    LeadState fromState;
    LeadState toState;
    String actor;
    ZonedDateTime timestamp;
    String reason;
    
    @Override
    public String toString() {
        return String.format("[%s] Lead %s: %s -> %s (by %s) - %s",
                timestamp,
                leadId,
                fromState,
                toState,
                actor,
                reason != null ? reason : "No reason provided");
    }
}

