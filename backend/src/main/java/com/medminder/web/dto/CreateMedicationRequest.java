package com.medminder.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMedicationRequest(
    @NotBlank String medicineName,
    @NotBlank String dosage,
    @NotBlank String form,
    @Min(0) int currentQuantity,
    @Min(0) int refillThreshold,
    String startDate,
    String endDate,
    String notes,
    Long catalogId
) {
}
