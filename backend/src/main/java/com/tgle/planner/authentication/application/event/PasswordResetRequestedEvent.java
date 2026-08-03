package com.tgle.planner.authentication.application.event;

public record PasswordResetRequestedEvent(
        String fullName,
        String email,
        String token
) {
}
