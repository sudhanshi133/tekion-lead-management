package com.tekion.demo.config;

import com.tekion.demo.adapter.InMemoryLeadRepository;
import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadSource;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.lead.valueObject.Email;
import com.tekion.demo.lead.valueObject.PhoneCoordinate;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Initializes the in-memory repository with sample data on application startup
 */
@Component
public class DataInitializer {

    private final InMemoryLeadRepository repository;

    public DataInitializer(InMemoryLeadRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing sample data...");

        // Lead 1: High-value referral lead
        Lead lead1 = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer001")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("John")
                .lastName("Smith")
                .email(new Email("john.smith@example.com"))
                .phone(new PhoneCoordinate("+1", "5551234567"))
                .source(LeadSource.REFERRAL)
                .state(LeadState.NEW)
                .vehicleInterest(new VehicleInterest("Toyota", "Camry", 2018, 12000.0))
                .createdAt(ZonedDateTime.now().minusHours(2))
                .updatedAt(ZonedDateTime.now().minusHours(2))
                .build();

        // Lead 2: Website lead
        Lead lead2 = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer001")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("Sarah")
                .lastName("Johnson")
                .email(new Email("sarah.j@example.com"))
                .phone(new PhoneCoordinate("+1", "5559876543"))
                .source(LeadSource.WEBSITE)
                .state(LeadState.NEW)
                .vehicleInterest(new VehicleInterest("Honda", "Accord", 2020, 8000.0))
                .createdAt(ZonedDateTime.now().minusHours(5))
                .updatedAt(ZonedDateTime.now().minusHours(5))
                .build();

        // Lead 3: Phone inquiry
        Lead lead3 = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer001")
                .tenantId("tenant1")
                .siteId("site1")
                .firstName("Michael")
                .lastName("Brown")
                .email(new Email("m.brown@example.com"))
                .phone(new PhoneCoordinate("+1", "5552468135"))
                .source(LeadSource.PHONE)
                .state(LeadState.CONTACTED)
                .vehicleInterest(new VehicleInterest("Ford", "F-150", 2019, 15000.0))
                .createdAt(ZonedDateTime.now().minusDays(1))
                .updatedAt(ZonedDateTime.now().minusHours(3))
                .build();

        // Lead 4: Walk-in customer
        Lead lead4 = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer002")
                .tenantId("tenant1")
                .siteId("site2")
                .firstName("Emily")
                .lastName("Davis")
                .email(new Email("emily.davis@example.com"))
                .phone(new PhoneCoordinate("+1", "5553691470"))
                .source(LeadSource.WALKIN)
                .state(LeadState.NEW)
                .vehicleInterest(new VehicleInterest("Nissan", "Altima", 2021, 5000.0))
                .createdAt(ZonedDateTime.now().minusHours(1))
                .updatedAt(ZonedDateTime.now().minusHours(1))
                .build();

        // Lead 5: High-value older lead
        Lead lead5 = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId("dealer002")
                .tenantId("tenant1")
                .siteId("site2")
                .firstName("Robert")
                .lastName("Wilson")
                .email(new Email("r.wilson@example.com"))
                .phone(new PhoneCoordinate("+1", "5558024691"))
                .source(LeadSource.REFERRAL)
                .state(LeadState.QUALIFIED)
                .vehicleInterest(new VehicleInterest("Chevrolet", "Silverado", 2017, 18000.0))
                .createdAt(ZonedDateTime.now().minusDays(3))
                .updatedAt(ZonedDateTime.now().minusDays(1))
                .build();

        // Save all leads
        repository.save(lead1);
        repository.save(lead2);
        repository.save(lead3);
        repository.save(lead4);
        repository.save(lead5);

        System.out.println("Sample data initialized successfully!");
        System.out.println("- 3 leads for dealer001");
        System.out.println("- 2 leads for dealer002");
    }
}

