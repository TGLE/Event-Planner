package com.tgle.planner.token.infrastructure;

import com.tgle.planner.core.properties.SecurityProperties;
import com.tgle.planner.token.application.service.TokenStateService;
import com.tgle.planner.token.domain.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final Clock clock;
    private final TokenStateService tokenStateService;
    private final SecurityProperties security;

    @Scheduled(cron = "${security.token.token-cleanup.cleanup-cron}")
    public void deleteExpiredTokens() {
        Instant now = Instant.now(clock);
        Duration resendResetWindow = security.token().otp().cooldownResetPeriod();
        Duration otpExpiration = security.token().otp().emailVerification().expiration();
        Instant otpThreshold = now.minus(resendResetWindow).minus(otpExpiration);

        int refreshDeleted = tokenStateService.deleteExpiredTokensByTypes(
                List.of(TokenType.REFRESH),
                now
        );

        int passwordResetVerifiedDeleted = tokenStateService.deleteExpiredTokensByTypes(
                List.of(TokenType.PASSWORD_RESET_VERIFIED),
                now
        );

        int otpDeleted = tokenStateService.deleteExpiredTokensByTypes(
                List.of(TokenType.EMAIL_VERIFY, TokenType.EMAIL_CHANGE, TokenType.PASSWORD_RESET),
                now
        );

        log.info("Token cleanup completed — refresh: {}, passwordResetVerified: {}, otp: {}",
                refreshDeleted, passwordResetVerifiedDeleted, otpDeleted);
    }
}
