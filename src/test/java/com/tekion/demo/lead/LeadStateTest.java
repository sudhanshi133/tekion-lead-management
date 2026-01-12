package com.tekion.demo.lead;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class LeadStateTest {

    @Test
    void shouldHaveCorrectDisplayNames() {
        assertEquals("New", LeadState.NEW.getDisplayName());
        assertEquals("Contacted", LeadState.CONTACTED.getDisplayName());
        assertEquals("Qualified", LeadState.QUALIFIED.getDisplayName());
        assertEquals("Converted", LeadState.CONVERTED.getDisplayName());
        assertEquals("Lost", LeadState.LOST.getDisplayName());
    }

    @ParameterizedTest
    @EnumSource(value = LeadState.class, names = {"CONVERTED", "LOST"})
    void shouldIdentifyTerminalStates(LeadState state) {
        assertTrue(state.isTerminal());
    }

    @ParameterizedTest
    @EnumSource(value = LeadState.class, names = {"NEW", "CONTACTED", "QUALIFIED"})
    void shouldIdentifyNonTerminalStates(LeadState state) {
        assertFalse(state.isTerminal());
    }

    @ParameterizedTest
    @CsvSource({
            "NEW, CONTACTED, true",
            "NEW, LOST, true",
            "NEW, QUALIFIED, false",
            "NEW, CONVERTED, false",
            "CONTACTED, QUALIFIED, true",
            "CONTACTED, LOST, true",
            "CONTACTED, NEW, false",
            "CONTACTED, CONVERTED, false",
            "QUALIFIED, CONVERTED, true",
            "QUALIFIED, LOST, true",
            "QUALIFIED, NEW, false",
            "QUALIFIED, CONTACTED, false",
            "CONVERTED, NEW, false",
            "CONVERTED, CONTACTED, false",
            "CONVERTED, QUALIFIED, false",
            "CONVERTED, LOST, false",
            "LOST, NEW, false",
            "LOST, CONTACTED, false",
            "LOST, QUALIFIED, false",
            "LOST, CONVERTED, false"
    })
    void shouldValidateStateTransitions(LeadState from, LeadState to, boolean expected) {
        assertEquals(expected, from.canTransitionTo(to));
    }

    @Test
    void shouldNotAllowTransitionFromTerminalStates() {
        assertFalse(LeadState.CONVERTED.canTransitionTo(LeadState.NEW));
        assertFalse(LeadState.CONVERTED.canTransitionTo(LeadState.CONTACTED));
        assertFalse(LeadState.CONVERTED.canTransitionTo(LeadState.QUALIFIED));
        assertFalse(LeadState.CONVERTED.canTransitionTo(LeadState.LOST));
        
        assertFalse(LeadState.LOST.canTransitionTo(LeadState.NEW));
        assertFalse(LeadState.LOST.canTransitionTo(LeadState.CONTACTED));
        assertFalse(LeadState.LOST.canTransitionTo(LeadState.QUALIFIED));
        assertFalse(LeadState.LOST.canTransitionTo(LeadState.CONVERTED));
    }
}

