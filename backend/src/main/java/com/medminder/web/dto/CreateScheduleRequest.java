package com.medminder.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record CreateScheduleRequest(
    Long medicationId,
    @NotBlank String scheduledTime,
    @DecimalMin("0.01") String doseAmount,
    @NotBlank String frequency,
    String startDate,
    String endDate
) {
}
