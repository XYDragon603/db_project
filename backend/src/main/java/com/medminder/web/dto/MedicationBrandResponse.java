package com.medminder.web.dto;

public record MedicationBrandResponse(Long brandId, String brandName, String manufacturer, String localRegistrationCode) {
}
