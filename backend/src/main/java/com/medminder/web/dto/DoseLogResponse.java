package com.medminder.web.dto;

public record DoseLogResponse(
    Long doseLogId,
    Long scheduleId,
    String status,
    int updatedQuantity
) {
}
