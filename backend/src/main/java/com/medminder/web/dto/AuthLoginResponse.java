package com.medminder.web.dto;

public record AuthLoginResponse(
    Long userId,
    String fullName,
    String email,
    String role
) {
}
