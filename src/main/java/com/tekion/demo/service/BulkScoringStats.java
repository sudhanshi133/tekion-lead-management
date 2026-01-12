package com.tekion.demo.service;

import lombok.Builder;
import lombok.Value;

/**
 * Statistics for bulk scoring performance
 */
@Value
@Builder
public class BulkScoringStats {
    int leadCount;
    long parallelTimeMs;
    long sequentialTimeMs;
    double speedupFactor;

    @Override
    public String toString() {
        return String.format(
                "Bulk Scoring Stats: %d leads | Parallel: %dms | Sequential: %dms | Speedup: %.2fx",
                leadCount, parallelTimeMs, sequentialTimeMs, speedupFactor);
    }
}

