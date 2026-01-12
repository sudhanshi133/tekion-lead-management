package com.tekion.demo.audit;

import com.tekion.demo.lead.LeadState;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Audit trail for tracking all state transitions
 * Thread-safe implementation using ConcurrentHashMap
 */
@Component
public class AuditTrail {

    private final ConcurrentHashMap<String, List<AuditEntry>> auditLog = new ConcurrentHashMap<>();

    /**
     * Log a state transition
     */
    public void logStateTransition(String leadId, LeadState fromState, LeadState toState, String actor, String reason) {
        AuditEntry entry = AuditEntry.builder()
                .leadId(leadId)
                .fromState(fromState)
                .toState(toState)
                .actor(actor)
                .timestamp(ZonedDateTime.now())
                .reason(reason)
                .build();

        auditLog.computeIfAbsent(leadId, k -> new ArrayList<>()).add(entry);
        
        // Log to console for visibility
        System.out.println("AUDIT: " + entry);
    }

    /**
     * Get audit history for a specific lead
     */
    public List<AuditEntry> getAuditHistory(String leadId) {
        return new ArrayList<>(auditLog.getOrDefault(leadId, new ArrayList<>()));
    }

    /**
     * Get all audit entries
     */
    public List<AuditEntry> getAllAuditEntries() {
        return auditLog.values().stream()
                .flatMap(List::stream)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Get audit entries by actor
     */
    public List<AuditEntry> getAuditEntriesByActor(String actor) {
        return auditLog.values().stream()
                .flatMap(List::stream)
                .filter(entry -> entry.getActor().equals(actor))
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Get count of state transitions for a lead
     */
    public int getTransitionCount(String leadId) {
        return auditLog.getOrDefault(leadId, new ArrayList<>()).size();
    }

    /**
     * Clear audit log (for testing)
     */
    public void clear() {
        auditLog.clear();
    }
}

