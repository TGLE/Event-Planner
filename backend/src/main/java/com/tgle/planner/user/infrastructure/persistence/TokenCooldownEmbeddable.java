package com.tgle.planner.user.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Instant;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TokenCooldownEmbeddable {

    @Column(name = "resend_count")
    private int resendCount;

    @Column(name = "cooldown_expires_at")
    private Instant lastResendAt;

    @Column(name = "cycle_started_at")
    private Instant cycleStartedAt;
}
