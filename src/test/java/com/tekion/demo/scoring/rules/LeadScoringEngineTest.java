package com.tekion.demo.scoring.rules;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import com.tekion.demo.scoring.ScoringResult;
import com.tekion.demo.scoring.ScoringRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeadScoringEngineTest {

    private LeadScoringEngine engine;

    @BeforeEach
    void setUp() {
        List<ScoringRule> rules = Arrays.asList(
                new SourceQualityRule(),
                new VehicleAgeRule(),
                new TradeInValueRule(),
                new EngagementRule(),
                new RecencyRule()
        );
        engine = new LeadScoringEngine(rules);
    }

    @Test
    void shouldScoreLeadWithAllRules() {
        Lead lead = TestDataBuilder.defaultLead()
                .source(LeadSource.REFERRAL)
                .createdAt(ZonedDateTime.now())
                .build();
        
        ScoringResult result = engine.score(lead);
        
        assertNotNull(result);
        assertTrue(result.getTotalScore() > 0);
        assertNotNull(result.getBreakdown());
    }

    @Test
    void shouldIncludeAllRulesInBreakdown() {
        Lead lead = TestDataBuilder.defaultLead().build();
        
        ScoringResult result = engine.score(lead);
        
        assertEquals(5, result.getBreakdown().size());
        assertTrue(result.getBreakdown().containsKey("Source Quality"));
        assertTrue(result.getBreakdown().containsKey("Vehicle Age"));
        assertTrue(result.getBreakdown().containsKey("Trade-In Value"));
        assertTrue(result.getBreakdown().containsKey("Engagement"));
        assertTrue(result.getBreakdown().containsKey("Recency"));
    }

    @Test
    void shouldScaleScoreTo100() {
        Lead lead = TestDataBuilder.defaultLead()
                .source(LeadSource.REFERRAL)
                .createdAt(ZonedDateTime.now())
                .build();
        
        ScoringResult result = engine.score(lead);
        
        assertTrue(result.getTotalScore() >= 0);
        assertTrue(result.getTotalScore() <= 100);
    }

    @Test
    void shouldGiveHigherScoreToHighQualityLead() {
        int currentYear = ZonedDateTime.now().getYear();

        Lead highQualityLead = TestDataBuilder.defaultLead()
                .source(LeadSource.REFERRAL)
                .vehicleInterest(new com.tekion.demo.lead.valueObject.VehicleInterest("Toyota", "Camry", currentYear - 6, 15000.0))
                .createdAt(ZonedDateTime.now())
                .build();

        Lead lowQualityLead = TestDataBuilder.defaultLead()
                .source(LeadSource.WALKIN)
                .vehicleInterest(new com.tekion.demo.lead.valueObject.VehicleInterest("Toyota", "Camry", currentYear, 0.0))
                .createdAt(ZonedDateTime.now().minusMonths(2))
                .build();

        ScoringResult highScore = engine.score(highQualityLead);
        ScoringResult lowScore = engine.score(lowQualityLead);

        assertTrue(highScore.getTotalScore() > lowScore.getTotalScore());
    }

    @Test
    void shouldHandleEmptyRulesList() {
        LeadScoringEngine emptyEngine = new LeadScoringEngine(List.of());
        Lead lead = TestDataBuilder.defaultLead().build();
        
        ScoringResult result = emptyEngine.score(lead);
        
        assertEquals(0.0, result.getTotalScore());
        assertTrue(result.getBreakdown().isEmpty());
    }

    @Test
    void shouldApplyWeightsCorrectly() {
        Lead lead = TestDataBuilder.defaultLead()
                .source(LeadSource.REFERRAL) // 1.0 * 0.2 = 0.2
                .build();
        
        ScoringResult result = engine.score(lead);
        
        // Source Quality: 1.0 * 0.2 = 0.2
        assertEquals(0.2, result.getBreakdown().get("Source Quality"), 0.01);
    }

    @Test
    void shouldCalculateTotalScoreCorrectly() {
        Lead lead = TestDataBuilder.defaultLead().build();
        
        ScoringResult result = engine.score(lead);
        
        double expectedTotal = result.getBreakdown().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum() * 100;
        
        assertEquals(expectedTotal, result.getTotalScore(), 0.01);
    }
}

