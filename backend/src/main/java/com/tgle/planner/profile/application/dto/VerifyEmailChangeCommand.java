package com.tgle.planner.profile.application.dto;

public record VerifyEmailChangeCommand(
        String tokenValue,
        Long userId
) {
}
