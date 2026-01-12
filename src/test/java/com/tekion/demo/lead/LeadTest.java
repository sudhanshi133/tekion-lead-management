package com.tekion.demo.lead;

import com.tekion.demo.TestDataBuilder;
import com.tekion.demo.lead.valueObject.Email;
import com.tekion.demo.lead.valueObject.PhoneCoordinate;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LeadTest {

    @Test
    void shouldCreateLeadWithBuilder() {
        ZonedDateTime now = ZonedDateTime.now();
        
        Lead lead = Lead.builder()
                .leadId("lead123")
                .dealerId("dealer123")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("John")
                .lastName("Doe")
                .email(new Email("john@example.com"))
                .phone(new PhoneCoordinate("+1", "1234567890"))
                .source(LeadSource.WEBSITE)
                .state(LeadState.NEW)
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2020, 5000.0))
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        assertEquals("lead123", lead.getLeadId());
        assertEquals("dealer123", lead.getDealerId());
        assertEquals("tenant1", lead.getTenantId());
        assertEquals("site1", lead.getSiteId());
        assertEquals("John", lead.getFirstName());
        assertEquals("Doe", lead.getLastName());
        assertEquals("john@example.com", lead.getEmail().getValue());
        assertEquals("+1", lead.getPhone().getCountryCode());
        assertEquals("1234567890", lead.getPhone().getNumber());
        assertEquals(LeadSource.WEBSITE, lead.getSource());
        assertEquals(LeadState.NEW, lead.getState());
        assertNotNull(lead.getVehicleInterest());
        assertEquals(now, lead.getCreatedAt());
        assertEquals(now, lead.getUpdatedAt());
    }

    @Test
    void shouldCreateLeadWithTestDataBuilder() {
        Lead lead = TestDataBuilder.defaultLead().build();
        
        assertNotNull(lead.getLeadId());
        assertNotNull(lead.getDealerId());
        assertNotNull(lead.getFirstName());
        assertNotNull(lead.getLastName());
        assertNotNull(lead.getSource());
        assertNotNull(lead.getState());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        Lead lead = Lead.builder()
                .leadId("lead123")
                .dealerId("dealer123")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("John")
                .lastName("Doe")
                .source(LeadSource.PHONE)
                .state(LeadState.NEW)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        
        assertNull(lead.getEmail());
        assertNull(lead.getPhone());
        assertNull(lead.getVehicleInterest());
    }

    @Test
    void shouldSupportSettersForMutableFields() {
        Lead lead = TestDataBuilder.defaultLead().build();
        
        lead.setState(LeadState.CONTACTED);
        assertEquals(LeadState.CONTACTED, lead.getState());
        
        ZonedDateTime newTime = ZonedDateTime.now().plusHours(1);
        lead.setUpdatedAt(newTime);
        assertEquals(newTime, lead.getUpdatedAt());
    }
}

