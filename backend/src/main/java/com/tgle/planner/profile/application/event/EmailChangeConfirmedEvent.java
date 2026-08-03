package com.tgle.planner.profile.application.event;

public record EmailChangeConfirmedEvent(
        String fullName,
        String currentEmail,
        String newEmail
) {
}
