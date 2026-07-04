package com.medminder.web.dto;

public record RefillAlertResponse(
    Long medicationId,
    String medicineName,
    int currentQuantity,
    int refillThreshold
) {
}
