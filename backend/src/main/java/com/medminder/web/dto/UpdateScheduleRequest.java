package com.medminder.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record UpdateScheduleRequest(
    @NotBlank String scheduledTime,
    @NotBlank @DecimalMin("0.01") String doseAmount
) {
}
