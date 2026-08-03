package com.tgle.planner.profile.application.dto;

public record ChangeEmailCommand(
        String newEmail,
        String currentPassword,
        Long userId
) {
}
