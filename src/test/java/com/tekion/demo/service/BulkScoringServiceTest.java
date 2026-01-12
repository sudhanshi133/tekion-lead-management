package com.tekion.demo.service;

import com.tekion.demo.lead.*;
import com.tekion.demo.scoring.ScoringResult;
import com.tekion.demo.scoring.rules.LeadScoringEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Bulk Scoring Service
 */
class BulkScoringServiceTest {

    @Mock
    private LeadScoringEngine scoringEngine;

    private BulkScoringService bulkScoringService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bulkScoringService = new BulkScoringService(scoringEngine);

        // Mock scoring engine to return predictable scores
        when(scoringEngine.score(any(Lead.class))).thenAnswer(invocation -> {
            Lead lead = invocation.getArgument(0);
            double score = lead.getLeadId().hashCode() % 100;
            return ScoringResult.builder()
                    .totalScore(score)
                    .build();
        });
    }

    @Test
    void testScoreBatch() {
        List<Lead> leads = createTestLeads(10);

        Map<String, ScoringResult> results = bulkScoringService.scoreBatch(leads);

        assertEquals(10, results.size());
        leads.forEach(lead -> {
            assertTrue(results.containsKey(lead.getLeadId()));
            assertNotNull(results.get(lead.getLeadId()));
        });
    }

    @Test
    void testScoreSequential() {
        List<Lead> leads = createTestLeads(10);

        Map<String, ScoringResult> results = bulkScoringService.scoreSequential(leads);

        assertEquals(10, results.size());
        leads.forEach(lead -> {
            assertTrue(results.containsKey(lead.getLeadId()));
            assertNotNull(results.get(lead.getLeadId()));
        });
    }

    @Test
    void testGetTopLeads() {
        List<Lead> leads = createTestLeads(20);

        List<Lead> topLeads = bulkScoringService.getTopLeads(leads, 5);

        assertEquals(5, topLeads.size());
    }

    @Test
    void testFilterByMinScore() {
        List<Lead> leads = createTestLeads(20);

        List<Lead> filteredLeads = bulkScoringService.filterByMinScore(leads, 50);

        assertTrue(filteredLeads.size() <= 20);
        // Verify all filtered leads meet the minimum score
        Map<String, ScoringResult> scores = bulkScoringService.scoreBatch(filteredLeads);
        filteredLeads.forEach(lead -> {
            assertTrue(scores.get(lead.getLeadId()).getTotalScore() >= 50);
        });
    }

    @Test
    void testGetPerformanceStats() {
        List<Lead> leads = createTestLeads(50);

        BulkScoringStats stats = bulkScoringService.getPerformanceStats(leads);

        assertNotNull(stats);
        assertEquals(50, stats.getLeadCount());
        assertTrue(stats.getParallelTimeMs() >= 0);
        assertTrue(stats.getSequentialTimeMs() >= 0);
        assertTrue(stats.getSpeedupFactor() > 0);
    }

    @Test
    void testEmptyBatch() {
        List<Lead> leads = new ArrayList<>();

        Map<String, ScoringResult> results = bulkScoringService.scoreBatch(leads);

        assertTrue(results.isEmpty());
    }

    @Test
    void testSingleLead() {
        List<Lead> leads = createTestLeads(1);

        Map<String, ScoringResult> results = bulkScoringService.scoreBatch(leads);

        assertEquals(1, results.size());
    }

    private List<Lead> createTestLeads(int count) {
        List<Lead> leads = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            leads.add(Lead.builder()
                    .leadId("lead" + i)
                    .dealerId("dealer001")
                    .tenantId("tenant001")
                    .siteId("site001")
                    .firstName("John" + i)
                    .lastName("Doe" + i)
                    .email(Email.builder()
                            .address("john" + i + "@example.com")
                            .verified(true)
                            .build())
                    .phone(Phone.builder()
                            .countryCode("+1")
                            .number("555000" + String.format("%04d", i))
                            .verified(true)
                            .build())
                    .source(LeadSource.builder()
                            .channel("WEB")
                            .campaign("test")
                            .build())
                    .state(LeadState.NEW)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build());
        }
        return leads;
    }
}

