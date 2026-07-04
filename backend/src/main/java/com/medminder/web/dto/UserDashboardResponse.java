package com.medminder.web.dto;

import java.util.List;

public record UserDashboardResponse(
    List<MedicationTaskResponse> todaysMedications,
    List<RefillAlertResponse> refillAlerts,
    AdherenceSummaryResponse adherenceSummary
) {
}
