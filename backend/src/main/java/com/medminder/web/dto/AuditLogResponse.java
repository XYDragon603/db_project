package com.medminder.web.dto;

public record AuditLogResponse(
    Long auditId,
    String createdAt,
    String actorName,
    String action,
    String targetTable,
    Long targetId,
    String details
) {
}
