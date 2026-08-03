package com.tgle.planner.user.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class TokenCooldown {
    @Builder.Default
    private final int resendCount = 0;
    private final Instant lastResendAt;
    private final Instant cycleStartedAt;

    public static TokenCooldown create(Instant now) {
        return TokenCooldown.builder()
                .resendCount(1)
                .lastResendAt(now)
                .cycleStartedAt(now)
                .build();
    }

    public TokenCooldown next(Duration resetWindow, Instant now) {
        if (isCycleExpired(resetWindow, now)) {
            return create(now);
        }
        return TokenCooldown.builder()
                .resendCount(resendCount + 1)
                .lastResendAt(now)
                .cycleStartedAt(cycleStartedAt)
                .build();
    }

    public boolean isCycleExpired(Duration resetWindow, Instant now) {
        if (cycleStartedAt == null) {
            return true;
        }
        Instant cycleEndsAt = cycleStartedAt.plus(resetWindow);
        return now.isAfter(cycleEndsAt);
    }

    public boolean isCooldownActive(List<Duration> cooldowns, Instant now) {
        if (resendCount == 0 || lastResendAt == null) {
            return false;
        }
        int index = Math.min(resendCount - 1, cooldowns.size() - 1);
        return now.isBefore(lastResendAt.plus(cooldowns.get(index)));
    }
}
