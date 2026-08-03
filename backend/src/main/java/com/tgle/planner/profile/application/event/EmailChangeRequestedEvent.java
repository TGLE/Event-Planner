package com.tgle.planner.profile.application.event;

public record EmailChangeRequestedEvent(
        String fullName,
        String newEmail,
        String token
) {
}
