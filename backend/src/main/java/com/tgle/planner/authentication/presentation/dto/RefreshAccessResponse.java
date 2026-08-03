package com.tgle.planner.authentication.presentation.dto;

import lombok.Builder;

@Builder
public record RefreshAccessResponse(
        String accessToken,
        String refreshToken,
        boolean rememberMe
) {
}
