package com.tgle.planner.authentication.application.dto;

public record VerifyOtpCommand(
        String email,
        String tokenValue
) {
}
