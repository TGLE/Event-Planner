package com.tgle.planner.authentication.application.dto;

public record LogoutCommand(
        String refreshToken,
        String deviceId
) {
}
