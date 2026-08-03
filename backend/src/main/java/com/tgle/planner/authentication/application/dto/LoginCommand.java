package com.tgle.planner.authentication.application.dto;

public record LoginCommand(
        String email,
        String password,
        boolean rememberMe,
        String deviceId
) {
}
