package com.tekion.demo.scoring.rules;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngagementRuleTest {

    private EngagementRule rule;

    @BeforeEach
    void setUp() {
        rule = new EngagementRule();
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("Engagement", rule.getName());
    }

    @Test
    void shouldHaveCorrectWeight() {
        assertEquals(0.15, rule.getWeight());
    }

    @Test
    void shouldReturnConstantScore() {
        Lead lead = TestDataBuilder.defaultLead().build();
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.5, score);
    }

    @Test
    void shouldReturnSameScoreForDifferentLeads() {
        Lead lead1 = TestDataBuilder.defaultLead().build();
        Lead lead2 = TestDataBuilder.defaultLead()
                .firstName("Jane")
                .lastName("Smith")
                .build();
        
        assertEquals(rule.evaluate(lead1), rule.evaluate(lead2));
    }
}

