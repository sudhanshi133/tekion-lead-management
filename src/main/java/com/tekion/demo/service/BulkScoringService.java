package com.tekion.demo.service;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.scoring.ScoringResult;
import com.tekion.demo.scoring.rules.LeadScoringEngine;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Bulk scoring service for high-performance batch operations
 * Uses parallel processing and CompletableFuture for async execution
 */
@Service
public class BulkScoringService {

    private final LeadScoringEngine scoringEngine;
    private final ExecutorService executorService;

    public BulkScoringService(LeadScoringEngine scoringEngine) {
        this.scoringEngine = scoringEngine;
        // Create thread pool with available processors
        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
    }

    /**
     * Score multiple leads in parallel
     * Returns a map of leadId -> ScoringResult
     */
    public Map<String, ScoringResult> scoreBatch(List<Lead> leads) {
        long startTime = System.currentTimeMillis();
        
        Map<String, ScoringResult> results = new ConcurrentHashMap<>();

        // Create CompletableFuture for each lead
        List<CompletableFuture<Void>> futures = leads.stream()
                .map(lead -> CompletableFuture.runAsync(() -> {
                    ScoringResult score = scoringEngine.score(lead);
                    results.put(lead.getLeadId(), score);
                }, executorService))
                .collect(Collectors.toList());

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        System.out.println(String.format(
                "Bulk scoring completed: %d leads in %d ms (avg: %.2f ms/lead)",
                leads.size(),
                endTime - startTime,
                (double) (endTime - startTime) / leads.size()));

        return results;
    }

    /**
     * Score leads sequentially (for comparison)
     */
    public Map<String, ScoringResult> scoreSequential(List<Lead> leads) {
        long startTime = System.currentTimeMillis();
        
        Map<String, ScoringResult> results = leads.stream()
                .collect(Collectors.toMap(
                        Lead::getLeadId,
                        scoringEngine::score));

        long endTime = System.currentTimeMillis();
        System.out.println(String.format(
                "Sequential scoring completed: %d leads in %d ms (avg: %.2f ms/lead)",
                leads.size(),
                endTime - startTime,
                (double) (endTime - startTime) / leads.size()));

        return results;
    }

    /**
     * Get top N leads by score from a batch
     */
    public List<Lead> getTopLeads(List<Lead> leads, int topN) {
        Map<String, ScoringResult> scores = scoreBatch(leads);
        
        return leads.stream()
                .sorted((a, b) -> {
                    double scoreA = scores.get(a.getLeadId()).getTotalScore();
                    double scoreB = scores.get(b.getLeadId()).getTotalScore();
                    return Double.compare(scoreB, scoreA);
                })
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Filter leads by minimum score threshold
     */
    public List<Lead> filterByMinScore(List<Lead> leads, double minScore) {
        Map<String, ScoringResult> scores = scoreBatch(leads);
        
        return leads.stream()
                .filter(lead -> scores.get(lead.getLeadId()).getTotalScore() >= minScore)
                .collect(Collectors.toList());
    }

    /**
     * Get performance statistics for bulk scoring
     */
    public BulkScoringStats getPerformanceStats(List<Lead> leads) {
        // Parallel scoring
        long parallelStart = System.currentTimeMillis();
        scoreBatch(leads);
        long parallelTime = System.currentTimeMillis() - parallelStart;

        // Sequential scoring
        long sequentialStart = System.currentTimeMillis();
        scoreSequential(leads);
        long sequentialTime = System.currentTimeMillis() - sequentialStart;

        return BulkScoringStats.builder()
                .leadCount(leads.size())
                .parallelTimeMs(parallelTime)
                .sequentialTimeMs(sequentialTime)
                .speedupFactor((double) sequentialTime / parallelTime)
                .build();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

