package com.tekion.demo.scoring.rules;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.scoring.ScoringRule;

public class TradeInValueRule implements ScoringRule {

    @Override
    public String getName() {
        return "Trade-In Value";
    }

    @Override
    public double getWeight() {
        return 0.25;
    }

    @Override
    public double evaluate(Lead lead) {
        if (lead.getVehicleInterest() == null || lead.getVehicleInterest().getTradeInValue() == null)
            return 0.1;

        double value = lead.getVehicleInterest().getTradeInValue();
        if (value > 10_000) return 1.0;
        if (value > 5_000) return 0.7;
        if (value > 0) return 0.4;
        return 0.1;
    }
}
