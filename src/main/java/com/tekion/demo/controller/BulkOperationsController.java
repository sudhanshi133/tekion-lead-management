package com.tekion.demo.controller;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.port.LeadPersistencePort;
import com.tekion.demo.scoring.ScoringResult;
import com.tekion.demo.service.BulkScoringService;
import com.tekion.demo.service.BulkScoringStats;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for bulk operations
 */
@RestController
@RequestMapping("/api/bulk")
public class BulkOperationsController {

    private final BulkScoringService bulkScoringService;
    private final LeadPersistencePort repository;

    public BulkOperationsController(BulkScoringService bulkScoringService, LeadPersistencePort repository) {
        this.bulkScoringService = bulkScoringService;
        this.repository = repository;
    }

    /**
     * Score all leads for a dealer in bulk
     * POST /api/bulk/score/{dealerId}
     */
    @PostMapping("/score/{dealerId}")
    public Map<String, ScoringResult> scoreBulk(@PathVariable String dealerId) {
        List<Lead> leads = repository.findByDealerId(dealerId);
        return bulkScoringService.scoreBatch(leads);
    }

    /**
     * Get top N leads by score
     * GET /api/bulk/top/{dealerId}?limit=10
     */
    @GetMapping("/top/{dealerId}")
    public List<Lead> getTopLeads(
            @PathVariable String dealerId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Lead> leads = repository.findByDealerId(dealerId);
        return bulkScoringService.getTopLeads(leads, limit);
    }

    /**
     * Filter leads by minimum score
     * GET /api/bulk/filter/{dealerId}?minScore=70
     */
    @GetMapping("/filter/{dealerId}")
    public List<Lead> filterByScore(
            @PathVariable String dealerId,
            @RequestParam(defaultValue = "70") double minScore) {
        List<Lead> leads = repository.findByDealerId(dealerId);
        return bulkScoringService.filterByMinScore(leads, minScore);
    }

    /**
     * Get performance statistics for bulk scoring
     * GET /api/bulk/stats/{dealerId}
     */
    @GetMapping("/stats/{dealerId}")
    public BulkScoringStats getPerformanceStats(@PathVariable String dealerId) {
        List<Lead> leads = repository.findByDealerId(dealerId);
        return bulkScoringService.getPerformanceStats(leads);
    }
}

