package com.tekion.demo.scoring;

import com.tekion.demo.lead.Lead;

public interface ScoringRule {

    String getName();

    double getWeight();

    double evaluate(Lead lead);
}
