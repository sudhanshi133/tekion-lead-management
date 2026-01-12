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
    void shouldGetLeadByIdAndDealerId() throws Exception {
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

        MvcResult createResult = mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lead)))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String leadId = objectMapper.readTree(response).get("lead").get("leadId").asText();

        // Then retrieve it
        mockMvc.perform(get("/api/leads/dealer999/" + leadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadId").value(leadId))
                .andExpect(jsonPath("$.dealerId").value("dealer999"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Williams"));
    }

    @Test
    void shouldThrowExceptionWhenLeadNotFound() throws Exception {
        mockMvc.perform(get("/api/leads/dealer123/nonexistent"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldListLeadsByDealer() throws Exception {
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

            mockMvc.perform(post("/api/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(lead)));
        }

        // List all leads for the dealer
        mockMvc.perform(get("/api/leads/dealer/" + dealerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }
}

