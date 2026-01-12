package com.tekion.demo.service;

import com.tekion.demo.audit.AuditTrail;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.port.LeadPersistencePort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * Service for managing lead state transitions with audit trail
 */
@Service
public class LeadStateService {

    private final LeadPersistencePort repository;
    private final AuditTrail auditTrail;

    public LeadStateService(LeadPersistencePort repository, AuditTrail auditTrail) {
        this.repository = repository;
        this.auditTrail = auditTrail;
    }

    /**
     * Transition lead to a new state with validation and audit logging
     */
    public Lead transitionState(String leadId, String dealerId, LeadState newState, String actor, String reason) {
        // Fetch the lead
        Lead lead = repository.findByIdAndDealerId(leadId, dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        LeadState currentState = lead.getState();

        // Validate transition
        if (!currentState.canTransitionTo(newState)) {
            throw new IllegalStateException(
                    String.format("Invalid state transition: %s -> %s", currentState, newState));
        }

        // Log the transition
        auditTrail.logStateTransition(leadId, currentState, newState, actor, reason);

        // Update the lead
        Lead updatedLead = Lead.builder()
                .leadId(lead.getLeadId())
                .dealerId(lead.getDealerId())
                .tenantId(lead.getTenantId())
                .siteId(lead.getSiteId())
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .source(lead.getSource())
                .state(newState)
                .vehicleInterest(lead.getVehicleInterest())
                .createdAt(lead.getCreatedAt())
                .updatedAt(ZonedDateTime.now())
                .build();

        repository.save(updatedLead);

        return updatedLead;
    }

    /**
     * Get audit history for a lead
     */
    public var getAuditHistory(String leadId) {
        return auditTrail.getAuditHistory(leadId);
    }
}

