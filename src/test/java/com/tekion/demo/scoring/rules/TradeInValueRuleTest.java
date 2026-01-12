package com.tekion.demo.scoring.rules;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TradeInValueRuleTest {

    private TradeInValueRule rule;

    @BeforeEach
    void setUp() {
        rule = new TradeInValueRule();
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("Trade-In Value", rule.getName());
    }

    @Test
    void shouldHaveCorrectWeight() {
        assertEquals(0.25, rule.getWeight());
    }

    @ParameterizedTest
    @CsvSource({
            "15000.0, 1.0",
            "10001.0, 1.0",
            "10000.0, 0.7",
            "7500.0, 0.7",
            "5001.0, 0.7",
            "5000.0, 0.4",
            "2500.0, 0.4",
            "1.0, 0.4",
            "0.0, 0.1"
    })
    void shouldEvaluateTradeInValue(double tradeInValue, double expectedScore) {
        Lead lead = TestDataBuilder.createLeadWithTradeInValue(tradeInValue);
        
        double score = rule.evaluate(lead);
        
        assertEquals(expectedScore, score);
    }

    @Test
    void shouldReturnLowScoreForNullVehicleInterest() {
        Lead lead = TestDataBuilder.defaultLead()
                .vehicleInterest(null)
                .build();
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.1, score);
    }

    @Test
    void shouldReturnLowScoreForNullTradeInValue() {
        Lead lead = TestDataBuilder.createLeadWithTradeInValue(null);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.1, score);
    }

    @Test
    void shouldGiveHighestScoreForHighValue() {
        Lead highValueLead = TestDataBuilder.createLeadWithTradeInValue(20000.0);
        Lead lowValueLead = TestDataBuilder.createLeadWithTradeInValue(1000.0);
        
        assertTrue(rule.evaluate(highValueLead) > rule.evaluate(lowValueLead));
    }

    @Test
    void shouldHandleNegativeValue() {
        Lead lead = TestDataBuilder.createLeadWithTradeInValue(-1000.0);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.1, score);
    }
}

