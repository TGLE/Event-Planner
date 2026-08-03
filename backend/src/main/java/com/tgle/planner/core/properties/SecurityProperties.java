package com.tgle.planner.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(
        JwtProperties jwt,
        TokenProperties token
) {
    public record JwtProperties(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey,
            Duration expiration
    ) {
    }

    public record TokenProperties(
            RefreshProperties refresh,
            OtpProperties otp,
            PasswordResetVerifiedProperties passwordResetVerified,
            TokenCleanupProperties tokenCleanup
    ) {
        public record RefreshProperties(
                Duration expiration,
                Duration rememberMeExpiration,
                String cookieName
        ) {
        }

        public record OtpProperties(
                EmailVerificationProperties emailVerification,
                EmailChangeProperties emailChange,
                PasswordResetProperties passwordReset,
                int length,
                List<Duration> cooldowns,
                Duration cooldownResetPeriod
        ) {
            public record EmailVerificationProperties(
                    Duration expiration
            ) {
            }

            public record EmailChangeProperties(
                    Duration expiration
            ) {
            }

            public record PasswordResetProperties(
                    Duration expiration
            ) {
            }
        }

        public record PasswordResetVerifiedProperties(
                Duration expiration,
                String cookieName
        ) {
        }

        public record TokenCleanupProperties(
                String cleanupCron
        ) {
        }
    }
}
