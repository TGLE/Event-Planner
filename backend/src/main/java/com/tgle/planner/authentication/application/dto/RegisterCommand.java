package com.tgle.planner.authentication.application.dto;

public record RegisterCommand(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
