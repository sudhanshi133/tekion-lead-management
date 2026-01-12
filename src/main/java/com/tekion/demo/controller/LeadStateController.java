package com.tekion.demo.controller;

import com.tekion.demo.audit.AuditEntry;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.service.LeadStateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for lead state transitions with audit trail
 */
@RestController
@RequestMapping("/api/leads/state")
public class LeadStateController {

    private final LeadStateService stateService;

    public LeadStateController(LeadStateService stateService) {
        this.stateService = stateService;
    }

    /**
     * Transition a lead to a new state
     * POST /api/leads/state/transition
     */
    @PostMapping("/transition")
    public Lead transitionState(@RequestBody StateTransitionRequest request) {
        return stateService.transitionState(
                request.getLeadId(),
                request.getDealerId(),
                request.getNewState(),
                request.getActor(),
                request.getReason()
        );
    }

    /**
     * Get audit history for a lead
     * GET /api/leads/state/audit/{leadId}
     */
    @GetMapping("/audit/{leadId}")
    public List<AuditEntry> getAuditHistory(@PathVariable String leadId) {
        return stateService.getAuditHistory(leadId);
    }

    /**
     * Request DTO for state transition
     */
    public static class StateTransitionRequest {
        private String leadId;
        private String dealerId;
        private LeadState newState;
        private String actor;
        private String reason;

        public String getLeadId() { return leadId; }
        public void setLeadId(String leadId) { this.leadId = leadId; }

        public String getDealerId() { return dealerId; }
        public void setDealerId(String dealerId) { this.dealerId = dealerId; }

        public LeadState getNewState() { return newState; }
        public void setNewState(LeadState newState) { this.newState = newState; }

        public String getActor() { return actor; }
        public void setActor(String actor) { this.actor = actor; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}

