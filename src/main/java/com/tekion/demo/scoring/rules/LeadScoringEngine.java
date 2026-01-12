package com.tekion.demo.scoring.rules;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.scoring.ScoringResult;
import com.tekion.demo.scoring.ScoringRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeadScoringEngine {

    private final List<ScoringRule> rules;

    public LeadScoringEngine(List<ScoringRule> rules) {
        this.rules = rules;
    }

    public ScoringResult score(Lead lead) {
        Map<String, Double> breakdown = new HashMap<>();
        double total = 0.0;

        for (ScoringRule rule : rules) {
            double score = rule.evaluate(lead) * rule.getWeight();
            breakdown.put(rule.getName(), score);
            total += score;
        }

        return ScoringResult.builder()
                .totalScore(total * 100) // convert to 0-100 scale
                .breakdown(breakdown)
                .build();
    }
}
