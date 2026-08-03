package com.tgle.planner.authentication.application.dto;

public record RefreshAccessTokenCommand(
        String tokenValue,
        String deviceId
) {
}
