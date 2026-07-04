package com.medminder.web.dto;

import jakarta.validation.constraints.NotBlank;

public record DoseLogRequest(
    Long scheduleId,
    @NotBlank String status,
    String scheduledDatetime
) {
}
