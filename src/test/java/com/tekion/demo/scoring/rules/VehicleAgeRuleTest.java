package com.tekion.demo.scoring.rules;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleAgeRuleTest {

    private VehicleAgeRule rule;

    @BeforeEach
    void setUp() {
        rule = new VehicleAgeRule();
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("Vehicle Age", rule.getName());
    }

    @Test
    void shouldHaveCorrectWeight() {
        assertEquals(0.25, rule.getWeight());
    }

    @Test
    void shouldGiveHighScoreForOldVehicles() {
        int currentYear = ZonedDateTime.now().getYear();
        Lead lead = TestDataBuilder.createLeadWithVehicleYear(currentYear - 5);
        
        double score = rule.evaluate(lead);
        
        assertEquals(1.0, score);
    }

    @Test
    void shouldGiveMediumScoreForMidAgeVehicles() {
        int currentYear = ZonedDateTime.now().getYear();
        Lead lead = TestDataBuilder.createLeadWithVehicleYear(currentYear - 3);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.6, score);
    }

    @Test
    void shouldGiveLowScoreForNewVehicles() {
        int currentYear = ZonedDateTime.now().getYear();
        Lead lead = TestDataBuilder.createLeadWithVehicleYear(currentYear - 1);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.2, score);
    }

    @Test
    void shouldGiveLowScoreForCurrentYearVehicles() {
        int currentYear = ZonedDateTime.now().getYear();
        Lead lead = TestDataBuilder.createLeadWithVehicleYear(currentYear);
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.2, score);
    }

    @Test
    void shouldReturnZeroForNullVehicleInterest() {
        Lead lead = TestDataBuilder.defaultLead()
                .vehicleInterest(null)
                .build();
        
        double score = rule.evaluate(lead);
        
        assertEquals(0.0, score);
    }

    @Test
    void shouldHandleVeryOldVehicles() {
        int currentYear = ZonedDateTime.now().getYear();
        Lead lead = TestDataBuilder.createLeadWithVehicleYear(currentYear - 10);
        
        double score = rule.evaluate(lead);
        
        assertEquals(1.0, score);
    }
}

