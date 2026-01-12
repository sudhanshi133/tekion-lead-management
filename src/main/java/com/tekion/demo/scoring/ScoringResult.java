package com.tekion.demo.scoring;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class ScoringResult {

    double totalScore;

    Map<String, Double> breakdown;
}