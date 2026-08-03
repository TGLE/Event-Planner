package com.tgle.planner.core.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    EMAIL_VERIFY("email/email-verify", "Verify your email"),
    EMAIL_CHANGE("email/email-change", "Change your email"),
    EMAIL_CHANGE_CONFIRMATION("email/email-change-confirmation", "Your email has been changed"),
    PASSWORD_RESET("email/password-reset", "Reset your password"),
    PASSWORD_RESET_CONFIRMATION("email/password-reset-confirmation", "Your password has been reset"),
    PASSWORD_CHANGE_CONFIRMATION("email/password-change-confirmation", "Your password has been changed");

    private final String path;
    private final String subject;
}
