package com.medminder.web.dto;

public record AdherenceSummaryResponse(
    int totalScheduledDoses,
    long totalTakenDoses,
    long totalMissedDoses,
    long totalSkippedDoses,
    long totalLateDoses,
    double adherenceRate
) {
}
