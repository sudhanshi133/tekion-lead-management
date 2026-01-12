package com.tekion.demo.scoring.rules;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.scoring.ScoringRule;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class RecencyRule implements ScoringRule {

    @Override
    public String getName() {
        return "Recency";
    }

    @Override
    public double getWeight() {
        return 0.15; // 15%
    }

    @Override
    public double evaluate(Lead lead) {
        Instant created = Instant.from(lead.getCreatedAt());
        if (created == null) return 0.1;

        long hours = Duration.between(created, Instant.now()).toHours();

        if (hours <= 24) return 1.0;
        if (hours <= 24 * 7) return 0.7;
        if (hours <= 24 * 30) return 0.4;
        return 0.1;
    }
}
