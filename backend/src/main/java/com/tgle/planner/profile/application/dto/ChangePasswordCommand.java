package com.tgle.planner.profile.application.dto;

public record ChangePasswordCommand(
        String currentPassword,
        String newPassword,
        Long userId
) {
}
