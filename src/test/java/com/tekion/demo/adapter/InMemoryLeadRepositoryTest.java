package com.tekion.demo.adapter;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryLeadRepositoryTest {

    private InMemoryLeadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLeadRepository();
    }

    @Test
    void shouldSaveAndRetrieveLead() {
        Lead lead = TestDataBuilder.defaultLead().build();
        
        Lead savedLead = repository.save(lead);
        
        assertNotNull(savedLead);
        assertEquals(lead.getLeadId(), savedLead.getLeadId());
    }

    @Test
    void shouldFindLeadByIdAndDealerId() {
        Lead lead = TestDataBuilder.defaultLead()
                .leadId("lead123")
                .dealerId("dealer123")
                .build();
        
        repository.save(lead);
        
        Optional<Lead> found = repository.findByIdAndDealerId("lead123", "dealer123");
        
        assertTrue(found.isPresent());
        assertEquals("lead123", found.get().getLeadId());
        assertEquals("dealer123", found.get().getDealerId());
    }

    @Test
    void shouldReturnEmptyWhenLeadNotFound() {
        Optional<Lead> found = repository.findByIdAndDealerId("nonexistent", "dealer123");
        
        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenDealerIdDoesNotMatch() {
        Lead lead = TestDataBuilder.defaultLead()
                .leadId("lead123")
                .dealerId("dealer123")
                .build();
        
        repository.save(lead);
        
        Optional<Lead> found = repository.findByIdAndDealerId("lead123", "wrongDealer");
        
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindLeadsByDealerIdAndState() {
        Lead lead1 = TestDataBuilder.defaultLead()
                .leadId("lead1")
                .dealerId("dealer123")
                .state(LeadState.NEW)
                .build();
        
        Lead lead2 = TestDataBuilder.defaultLead()
                .leadId("lead2")
                .dealerId("dealer123")
                .state(LeadState.NEW)
                .build();
        
        Lead lead3 = TestDataBuilder.defaultLead()
                .leadId("lead3")
                .dealerId("dealer123")
                .state(LeadState.CONTACTED)
                .build();
        
        repository.save(lead1);
        repository.save(lead2);
        repository.save(lead3);
        
        List<Lead> newLeads = repository.findByDealerIdAndState("dealer123", LeadState.NEW);
        
        assertEquals(2, newLeads.size());
        assertTrue(newLeads.stream().allMatch(l -> l.getState() == LeadState.NEW));
    }

    @Test
    void shouldReturnEmptyListWhenNoLeadsMatchState() {
        Lead lead = TestDataBuilder.defaultLead()
                .dealerId("dealer123")
                .state(LeadState.NEW)
                .build();
        
        repository.save(lead);
        
        List<Lead> contactedLeads = repository.findByDealerIdAndState("dealer123", LeadState.CONTACTED);
        
        assertTrue(contactedLeads.isEmpty());
    }

    @Test
    void shouldFindLeadsByDealerIdOrderByScore() {
        Lead lead1 = TestDataBuilder.defaultLead()
                .leadId("lead1")
                .dealerId("dealer123")
                .build();
        
        Lead lead2 = TestDataBuilder.defaultLead()
                .leadId("lead2")
                .dealerId("dealer123")
                .build();
        
        repository.save(lead1);
        repository.save(lead2);
        
        List<Lead> leads = repository.findByDealerIdOrderByScore("dealer123", 10);
        
        assertEquals(2, leads.size());
    }

    @Test
    void shouldRespectLimitInFindByDealerIdOrderByScore() {
        for (int i = 0; i < 5; i++) {
            Lead lead = TestDataBuilder.defaultLead()
                    .leadId("lead" + i)
                    .dealerId("dealer123")
                    .build();
            repository.save(lead);
        }
        
        List<Lead> leads = repository.findByDealerIdOrderByScore("dealer123", 3);
        
        assertEquals(3, leads.size());
    }

    @Test
    void shouldIsolateLeadsByDealer() {
        Lead dealer1Lead = TestDataBuilder.defaultLead()
                .leadId("lead1")
                .dealerId("dealer1")
                .build();
        
        Lead dealer2Lead = TestDataBuilder.defaultLead()
                .leadId("lead2")
                .dealerId("dealer2")
                .build();
        
        repository.save(dealer1Lead);
        repository.save(dealer2Lead);
        
        List<Lead> dealer1Leads = repository.findByDealerIdOrderByScore("dealer1", 10);
        List<Lead> dealer2Leads = repository.findByDealerIdOrderByScore("dealer2", 10);
        
        assertEquals(1, dealer1Leads.size());
        assertEquals(1, dealer2Leads.size());
        assertEquals("dealer1", dealer1Leads.get(0).getDealerId());
        assertEquals("dealer2", dealer2Leads.get(0).getDealerId());
    }

    @Test
    void shouldUpdateExistingLead() {
        Lead lead = TestDataBuilder.defaultLead()
                .leadId("lead123")
                .dealerId("dealer123")
                .state(LeadState.NEW)
                .build();
        
        repository.save(lead);
        
        lead.setState(LeadState.CONTACTED);
        repository.save(lead);
        
        Optional<Lead> found = repository.findByIdAndDealerId("lead123", "dealer123");
        
        assertTrue(found.isPresent());
        assertEquals(LeadState.CONTACTED, found.get().getState());
    }
}

