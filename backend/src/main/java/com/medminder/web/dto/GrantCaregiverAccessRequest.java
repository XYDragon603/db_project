package com.medminder.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GrantCaregiverAccessRequest(
    @NotBlank @Email String caregiverEmail
) {
}
