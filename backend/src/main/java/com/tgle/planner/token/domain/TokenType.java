package com.tgle.planner.token.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    REFRESH("Refresh token", TokenGenerationType.OPAQUE, true),
    EMAIL_VERIFY("Email verification token", TokenGenerationType.OTP, false),
    PASSWORD_RESET("Password reset token", TokenGenerationType.OTP, false),
    PASSWORD_RESET_VERIFIED("Password reset verified token", TokenGenerationType.OPAQUE, true),
    EMAIL_CHANGE("Email change token", TokenGenerationType.OTP, false);

    private final String description;
    private final TokenGenerationType tokenGenerationType;
    private final boolean storedInCookie;
}
