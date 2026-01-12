package com.tekion.demo.controller;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.lead.LeadState;
import com.tekion.demo.notification.Notification;
import com.tekion.demo.notification.NotificationType;
import com.tekion.demo.port.LeadPersistencePort;
import com.tekion.demo.port.NotificationPort;
import com.tekion.demo.scoring.rules.LeadScoringEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    @Autowired
    private LeadPersistencePort repository;

    @Autowired
    private NotificationPort notificationRouter;

    @Autowired
    private LeadScoringEngine scoringEngine;

    // Create a lead
    @PostMapping
    public Map<String, Object> createLead(@RequestBody Lead lead) {

        // 1️⃣ Assign generated ID and timestamps
        lead = Lead.builder()
                .leadId(UUID.randomUUID().toString())
                .dealerId(lead.getDealerId())
                .tenantId(lead.getTenantId())
                .siteId(lead.getSiteId())
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .source(lead.getSource())
                .state(LeadState.NEW)
                .vehicleInterest(lead.getVehicleInterest())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        // 2️⃣ Save
        repository.save(lead);

        // 3️⃣ Score
        var score = scoringEngine.score(lead);

        // 4️⃣ Notify dealer
        Notification notification = Notification.builder()
                .recipient(lead.getDealerId())
                .message("New lead: " + lead.getFirstName() + " " + lead.getLastName())
                .type(NotificationType.EMAIL)
                .build();

        var notificationResult = notificationRouter.send(notification);

        return Map.of(
                "lead", lead,
                "score", score,
                "notification", notificationResult
        );
    }

    // Get lead by ID
    @GetMapping("/{dealerId}/{leadId}")
    public Lead getLead(@PathVariable String dealerId, @PathVariable String leadId) {
        return repository.findByIdAndDealerId(leadId, dealerId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
    }

    // List leads by dealer
    @GetMapping("/dealer/{dealerId}")
    public List<Lead> listLeads(@PathVariable String dealerId) {
        return repository.findByDealerIdOrderByScore(dealerId, 100);
    }
}
