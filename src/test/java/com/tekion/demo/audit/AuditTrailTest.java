package com.tekion.demo.audit;

import com.tekion.demo.lead.LeadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Audit Trail
 */
class AuditTrailTest {

    private AuditTrail auditTrail;

    @BeforeEach
    void setUp() {
        auditTrail = new AuditTrail();
    }

    @Test
    void testLogStateTransition() {
        auditTrail.logStateTransition("lead1", LeadState.NEW, LeadState.CONTACTED, "user1", "Initial contact");

        List<AuditEntry> history = auditTrail.getAuditHistory("lead1");
        assertEquals(1, history.size());

        AuditEntry entry = history.get(0);
        assertEquals("lead1", entry.getLeadId());
        assertEquals(LeadState.NEW, entry.getFromState());
        assertEquals(LeadState.CONTACTED, entry.getToState());
        assertEquals("user1", entry.getActor());
        assertEquals("Initial contact", entry.getReason());
        assertNotNull(entry.getTimestamp());
    }

    @Test
    void testMultipleTransitions() {
        auditTrail.logStateTransition("lead1", LeadState.NEW, LeadState.CONTACTED, "user1", "First contact");
        auditTrail.logStateTransition("lead1", LeadState.CONTACTED, LeadState.QUALIFIED, "user1", "Qualified");
        auditTrail.logStateTransition("lead1", LeadState.QUALIFIED, LeadState.CONVERTED, "user2", "Sale completed");

        List<AuditEntry> history = auditTrail.getAuditHistory("lead1");
        assertEquals(3, history.size());
    }

    @Test
    void testGetAuditEntriesByActor() {
        auditTrail.logStateTransition("lead1", LeadState.NEW, LeadState.CONTACTED, "user1", "Contact");
        auditTrail.logStateTransition("lead2", LeadState.NEW, LeadState.CONTACTED, "user2", "Contact");
        auditTrail.logStateTransition("lead3", LeadState.NEW, LeadState.CONTACTED, "user1", "Contact");

        List<AuditEntry> user1Entries = auditTrail.getAuditEntriesByActor("user1");
        assertEquals(2, user1Entries.size());

        List<AuditEntry> user2Entries = auditTrail.getAuditEntriesByActor("user2");
        assertEquals(1, user2Entries.size());
    }

    @Test
    void testGetAllAuditEntries() {
        auditTrail.logStateTransition("lead1", LeadState.NEW, LeadState.CONTACTED, "user1", "Contact");
        auditTrail.logStateTransition("lead2", LeadState.NEW, LeadState.CONTACTED, "user2", "Contact");

        List<AuditEntry> allEntries = auditTrail.getAllAuditEntries();
        assertEquals(2, allEntries.size());
    }

    @Test
    void testGetTransitionCount() {
        auditTrail.logStateTransition("lead1", LeadState.NEW, LeadState.CONTACTED, "user1", "Contact");
        auditTrail.logStateTransition("lead1", LeadState.CONTACTED, LeadState.QUALIFIED, "user1", "Qualified");

        assertEquals(2, auditTrail.getTransitionCount("lead1"));
        assertEquals(0, auditTrail.getTransitionCount("lead2"));
    }

    @Test
    void testClear() {
        auditTrail.logStateTransition("lead1", LeadState.NEW, LeadState.CONTACTED, "user1", "Contact");
        assertEquals(1, auditTrail.getAllAuditEntries().size());

        auditTrail.clear();
        assertEquals(0, auditTrail.getAllAuditEntries().size());
    }

    @Test
    void testEmptyHistory() {
        List<AuditEntry> history = auditTrail.getAuditHistory("nonexistent");
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }
}

