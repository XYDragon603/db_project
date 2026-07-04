package com.medminder.web.dto;

public record ScheduleResponse(
    Long scheduleId,
    String scheduledTime,
    String doseAmount,
    String frequency,
    boolean active
) {
}
