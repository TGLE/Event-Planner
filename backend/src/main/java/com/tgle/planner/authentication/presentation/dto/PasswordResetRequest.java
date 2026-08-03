package com.tgle.planner.authentication.presentation.dto;

import com.tgle.planner.authentication.presentation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch(password = "newPassword", confirmPassword = "confirmPassword")
public record PasswordResetRequest(
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be at least 8 characters long")
        String newPassword,

        @NotBlank(message = "Please confirm your password")
        String confirmPassword
) {
}
