package com.medminder.web.dto;

public record MedicationAdherenceSummaryResponse(
    Long medicationId,
    String medicineName,
    String dosage,
    int scheduledDoses,
    long takenCount,
    long missedCount,
    long skippedCount,
    long lateCount,
    double adherenceRate
) {
}
