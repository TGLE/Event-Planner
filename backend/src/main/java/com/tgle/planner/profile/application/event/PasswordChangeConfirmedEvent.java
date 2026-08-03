package com.tgle.planner.profile.application.event;

public record PasswordChangeConfirmedEvent(
        String fullName,
        String email
) {
}
