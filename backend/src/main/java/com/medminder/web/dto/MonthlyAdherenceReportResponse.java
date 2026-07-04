package com.medminder.web.dto;

import java.util.List;

public record MonthlyAdherenceReportResponse(
    String month,
    AdherenceSummaryResponse summary,
    List<MedicationAdherenceSummaryResponse> medications
) {
}
