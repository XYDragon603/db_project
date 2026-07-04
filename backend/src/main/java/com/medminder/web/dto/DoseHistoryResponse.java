package com.medminder.web.dto;

public record DoseHistoryResponse(
    Long doseLogId,
    String scheduledDatetime,
    String medicineName,
    String dosage,
    String status,
    String actualTakenTime
) {
}
