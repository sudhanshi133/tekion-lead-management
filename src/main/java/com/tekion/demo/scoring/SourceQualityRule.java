package com.tekion.demo.scoring;

import com.tekion.demo.lead.Lead;

public class SourceQualityRule implements ScoringRule {

    @Override
    public String getName() {
        return "Source Quality";
    }

    @Override
    public double getWeight() {
        return 0.2;
    }

    @Override
    public double evaluate(Lead lead) {
        if (lead.getSource() == null) return 0.0;
        return switch (lead.getSource()) {
            case REFERRAL -> 1.0;
            case WEBSITE -> 0.7;
            case PHONE -> 0.5;
            case WALKIN -> 0.3;
        };
    }
}
