package com.tgle.planner.authentication.application.dto;

import lombok.Builder;

public record AuthenticationResult(
        String accessToken,
        String refreshToken,
        boolean rememberMe
) {
}
