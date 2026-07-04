package com.medminder.web.dto;

public record CaregiverAccessResponse(
    Long accessId,
    Long caregiverUserId,
    String caregiverName,
    String caregiverEmail,
    String accessStatus,
    String grantedAt
) {
}
