package com.medminder.web.dto;

public record MedicationResponse(
    Long medicationId,
    String medicineName,
    String dosage,
    String form,
    int currentQuantity,
    int refillThreshold,
    boolean active,
    String startDate,
    String endDate,
    String notes
) {
}
