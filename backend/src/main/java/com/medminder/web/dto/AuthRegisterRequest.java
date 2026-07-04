package com.medminder.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
    @NotBlank @Size(max = 150) String fullName,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @Size(max = 30) String phone
) {
}
