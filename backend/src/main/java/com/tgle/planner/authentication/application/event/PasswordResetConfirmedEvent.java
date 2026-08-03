package com.tgle.planner.authentication.application.event;

public record PasswordResetConfirmedEvent(
        String fullName,
        String email
) {
}
