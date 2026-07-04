package com.medminder.web.dto;

public record CaregiverPatientOverviewResponse(
    Long patientUserId,
    String patientName,
    long todayPending,
    long todayMissed,
    long refillAlerts
) {
}
