package com.tekion.demo.scoring.rules;

import com.tekion.demo.lead.Lead;
import com.tekion.demo.scoring.ScoringRule;

import java.time.Instant;
import java.time.ZoneId;

public class VehicleAgeRule implements ScoringRule {

    @Override
    public String getName() {
        return "Vehicle Age";
    }

    @Override
    public double getWeight() {
        return 0.25; // 25%
    }

    @Override
    public double evaluate(Lead lead) {
        if (lead.getVehicleInterest() == null) return 0.0;
        int vehicleYear = lead.getVehicleInterest().getYear();
        int currentYear = Instant.now().atZone(ZoneId.systemDefault()).getYear();
        int age = currentYear - vehicleYear;

        if (age >= 5) return 1.0;
        if (age >= 3) return 0.6;
        return 0.2;
    }
}

