package com.tekion.demo.scoring.rules;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RecencyRuleTest {

    private RecencyRule rule;

    @BeforeEach
    void setUp() {
        rule = new RecencyRule();
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("Recency", rule.getName());
    }

    @Test
    void shouldHaveCorrectWeight() {
        assertEquals(0.15, rule.getWeight());
    }

    @Test
    void shouldGiveHighScoreForRecentLeads() {
        ZonedDateTime now = ZonedDateTime.now();
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(now);
        
        double score = rule.evaluate(lead);
        
        assertEquals(1.0, score);
    }

    @Test
    void shouldGiveHighScoreForLeadsWithin24Hours() {
        ZonedDateTime twentyHoursAgo = ZonedDateTime.now().minusHours(20);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(twentyHoursAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(1.0, score);
    }

    @Test
    void shouldGiveMediumScoreForLeadsWithinWeek() {
        ZonedDateTime threeDaysAgo = ZonedDateTime.now().minusDays(3);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(threeDaysAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.7, score);
    }

    @Test
    void shouldGiveLowScoreForLeadsWithinMonth() {
        ZonedDateTime twoWeeksAgo = ZonedDateTime.now().minusWeeks(2);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(twoWeeksAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.4, score);
    }

    @Test
    void shouldGiveVeryLowScoreForOldLeads() {
        ZonedDateTime twoMonthsAgo = ZonedDateTime.now().minusMonths(2);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(twoMonthsAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.1, score);
    }

    @Test
    void shouldHandleEdgeCaseAt24Hours() {
        ZonedDateTime exactlyOneDayAgo = ZonedDateTime.now().minusHours(24);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(exactlyOneDayAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(1.0, score);
    }

    @Test
    void shouldHandleEdgeCaseAt7Days() {
        ZonedDateTime exactlySevenDaysAgo = ZonedDateTime.now().minusDays(7);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(exactlySevenDaysAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.7, score);
    }

    @Test
    void shouldHandleEdgeCaseAt30Days() {
        ZonedDateTime exactlyThirtyDaysAgo = ZonedDateTime.now().minusDays(30);
        Lead lead = TestDataBuilder.createLeadWithCreatedAt(exactlyThirtyDaysAgo);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.4, score);
    }
}

