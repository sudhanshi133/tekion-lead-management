package com.tekion.demo.scoring;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SourceQualityRuleTest {

    private SourceQualityRule rule;

    @BeforeEach
    void setUp() {
        rule = new SourceQualityRule();
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("Source Quality", rule.getName());
    }

    @Test
    void shouldHaveCorrectWeight() {
        assertEquals(0.2, rule.getWeight());
    }

    @ParameterizedTest
    @CsvSource({
            "REFERRAL, 1.0",
            "WEBSITE, 0.7",
            "PHONE, 0.5",
            "WALKIN, 0.3"
    })
    void shouldEvaluateSourceQuality(LeadSource source, double expectedScore) {
        Lead lead = TestDataBuilder.createLeadWithSource(source);
        
        double score = rule.evaluate(lead);
        
        assertEquals(expectedScore, score);
    }

    @Test
    void shouldReturnZeroForNullSource() {
        Lead lead = TestDataBuilder.defaultLead()
                .source(null)
                .build();
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.0, score);
    }

    @Test
    void shouldGiveHighestScoreToReferrals() {
        Lead referralLead = TestDataBuilder.createLeadWithSource(LeadSource.REFERRAL);
        Lead websiteLead = TestDataBuilder.createLeadWithSource(LeadSource.WEBSITE);
        
        assertTrue(rule.evaluate(referralLead) > rule.evaluate(websiteLead));
    }

    @Test
    void shouldGiveLowestScoreToWalkIn() {
        Lead walkinLead = TestDataBuilder.createLeadWithSource(LeadSource.WALKIN);
        Lead phoneLead = TestDataBuilder.createLeadWithSource(LeadSource.PHONE);
        
        assertTrue(rule.evaluate(phoneLead) > rule.evaluate(walkinLead));
    }
}

