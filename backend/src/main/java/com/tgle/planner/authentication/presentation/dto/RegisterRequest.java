package com.tgle.planner.authentication.presentation.dto;

import com.tgle.planner.authentication.presentation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch(password = "password", confirmPassword = "confirmPassword")
public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 64, message = "First name must be at least 2 characters long")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 64, message = "Last name must be at least 2 characters long")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank(message = "Please confirm your password")
        String confirmPassword
) {
}
