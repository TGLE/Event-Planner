package com.tgle.planner.notification.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long recipientId;
    private final Long referenceId;
    private final NotificationType type;
    private final Instant sentAt;
    private final Instant readAt;

    public static Notification create(
            Long recipientId,
            Long referenceId,
            NotificationType type,
            Instant sentAt
    ) {
        return Notification.builder()
                .recipientId(recipientId)
                .referenceId(referenceId)
                .type(type)
                .sentAt(sentAt)
                .build();
    }
}
