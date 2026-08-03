package com.tgle.planner.authentication.application.dto;

public record PasswordResetCommand(
        String newPassword,
        String confirmPassword,
        String resetToken
) {
}
