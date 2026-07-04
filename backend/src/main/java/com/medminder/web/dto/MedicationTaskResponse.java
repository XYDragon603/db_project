package com.medminder.web.dto;

public record MedicationTaskResponse(
    Long scheduleId,
    Long medicationId,
    String medicineName,
    String dosage,
    String scheduledTime,
    String doseAmount,
    String status
) {
}
