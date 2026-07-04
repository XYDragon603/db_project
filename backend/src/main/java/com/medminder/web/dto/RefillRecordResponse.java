package com.medminder.web.dto;

public record RefillRecordResponse(
    Long refillId,
    Long medicationId,
    int quantityAdded,
    int updatedQuantity
) {
}
