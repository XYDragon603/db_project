package com.medminder.web.dto;

public record ProfileResponse(
    Long userId,
    String fullName,
    String email,
    String phone,
    boolean active,
    String createdAt
) {
}
