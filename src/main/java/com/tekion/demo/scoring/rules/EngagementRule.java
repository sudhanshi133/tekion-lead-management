package com.tekion.demo.scoring.rules;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.scoring.ScoringRule;
import org.springframework.stereotype.Component;

@Component
public class EngagementRule implements ScoringRule {

    @Override
    public String getName() {
        return "Engagement";
    }

    @Override
    public double getWeight() {
        return 0.15; // 15%
    }

    @Override
    public double evaluate(Lead lead) {
        return 0.5;
    }
}

