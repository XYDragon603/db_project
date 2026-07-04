package com.medminder.web.dto;

import java.util.List;

public record AdminUserResponse(
    Long userId,
    String fullName,
    String email,
    String phone,
    boolean active,
    List<String> roles,
    String createdAt
) {
}
