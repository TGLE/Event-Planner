package com.tgle.planner.authentication.application.event;

public record VerifyEmailRequestedEvent(
        String fullName,
        String email,
        String token
) {
}
