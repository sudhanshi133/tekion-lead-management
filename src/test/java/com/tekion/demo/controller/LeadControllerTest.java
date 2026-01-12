package com.tekion.demo.controller;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LeadControllerTest {

    private LeadController controller;

    @BeforeEach
    void setUp() {
        controller = new LeadController();
    }

    @Test
    void shouldCreateLeadSuccessfully() {
        Lead lead = Lead.builder()
                .dealerId("dealer123")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("John")
                .lastName("Doe")
                .source(LeadSource.WEBSITE)
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2020, 5000.0))
                .build();

        Map<String, Object> result = controller.createLead(lead);

        assertNotNull(result);
        assertTrue(result.containsKey("lead"));
        assertTrue(result.containsKey("score"));
        assertTrue(result.containsKey("notification"));

        Lead createdLead = (Lead) result.get("lead");
        assertNotNull(createdLead.getLeadId());
        assertEquals("dealer123", createdLead.getDealerId());
        assertEquals("John", createdLead.getFirstName());
        assertEquals("Doe", createdLead.getLastName());
        assertEquals(com.tekion.demo.lead.LeadState.NEW, createdLead.getState());
    }

    @Test
    void shouldAssignLeadIdAndTimestamps() {
        Lead lead = Lead.builder()
                .dealerId("dealer456")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("Jane")
                .lastName("Smith")
                .source(LeadSource.REFERRAL)
                .vehicleInterest(new VehicleInterest("Honda", "Accord", 2019, 8000.0))
                .build();

        Map<String, Object> result = controller.createLead(lead);

        Lead createdLead = (Lead) result.get("lead");
        assertNotNull(createdLead.getLeadId());
        assertNotNull(createdLead.getCreatedAt());
        assertNotNull(createdLead.getUpdatedAt());
    }

    @Test
    void shouldScoreLeadOnCreation() {
        Lead lead = Lead.builder()
                .dealerId("dealer789")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("Bob")
                .lastName("Johnson")
                .source(LeadSource.REFERRAL)
                .vehicleInterest(new VehicleInterest("Ford", "F-150", 2018, 12000.0))
                .build();

        Map<String, Object> result = controller.createLead(lead);

        assertTrue(result.containsKey("score"));
        com.tekion.demo.scoring.ScoringResult score = (com.tekion.demo.scoring.ScoringResult) result.get("score");
        assertNotNull(score);
        assertTrue(score.getTotalScore() > 0);
        assertNotNull(score.getBreakdown());
        assertTrue(score.getBreakdown().containsKey("Source Quality"));
        assertTrue(score.getBreakdown().containsKey("Vehicle Age"));
        assertTrue(score.getBreakdown().containsKey("Trade-In Value"));
        assertTrue(score.getBreakdown().containsKey("Engagement"));
        assertTrue(score.getBreakdown().containsKey("Recency"));
    }

    @Test
    void shouldGetLeadByIdAndDealerId() {
        // First create a lead
        Lead lead = Lead.builder()
                .dealerId("dealer999")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("Alice")
                .lastName("Williams")
                .source(LeadSource.PHONE)
                .vehicleInterest(new VehicleInterest("Nissan", "Altima", 2021, 3000.0))
                .build();

        Map<String, Object> createResult = controller.createLead(lead);
        Lead createdLead = (Lead) createResult.get("lead");
        String leadId = createdLead.getLeadId();

        // Then retrieve it
        Lead retrievedLead = controller.getLead("dealer999", leadId);

        assertNotNull(retrievedLead);
        assertEquals(leadId, retrievedLead.getLeadId());
        assertEquals("dealer999", retrievedLead.getDealerId());
        assertEquals("Alice", retrievedLead.getFirstName());
        assertEquals("Williams", retrievedLead.getLastName());
    }

    @Test
    void shouldThrowExceptionWhenLeadNotFound() {
        assertThrows(RuntimeException.class, () -> {
            controller.getLead("dealer123", "nonexistent");
        });
    }

    @Test
    void shouldListLeadsByDealer() {
        // Create multiple leads for the same dealer
        String dealerId = "dealer555";

        for (int i = 0; i < 3; i++) {
            Lead lead = Lead.builder()
                    .dealerId(dealerId)
                    .tenantId("tenant1")
                    .siteId("site1")
                    .firstName("Customer" + i)
                    .lastName("Test")
                    .source(LeadSource.WEBSITE)
                    .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2020, 5000.0))
                    .build();

            controller.createLead(lead);
        }

        // List all leads for the dealer
        List<Lead> leads = controller.listLeads(dealerId);

        assertNotNull(leads);
        assertTrue(leads.size() >= 3);
        assertTrue(leads.stream().allMatch(l -> l.getDealerId().equals(dealerId)));
    }
}

