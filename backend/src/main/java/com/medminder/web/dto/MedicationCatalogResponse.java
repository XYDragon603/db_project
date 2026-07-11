package com.medminder.web.dto;

import java.util.List;

public record MedicationCatalogResponse(
    Long catalogId,
    String countryCode,
    String genericName,
    String dosageForm,
    String strength,
    boolean prescriptionRequired,
    List<MedicationBrandResponse> brands
) {
}
