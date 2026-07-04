package com.medminder.web.dto;

import jakarta.validation.constraints.Min;

public record CreateRefillRequest(
    Long medicationId,
    @Min(1) int quantityAdded,
    String note
) {
}
