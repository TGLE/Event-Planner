package com.tgle.planner.profile.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String newEmail,

        @NotBlank(message = "Current password is required")
        String currentPassword
) {
}
