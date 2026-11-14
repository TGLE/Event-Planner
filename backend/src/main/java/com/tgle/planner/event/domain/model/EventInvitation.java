package com.tgle.planner.event.domain.model;

import com.tgle.planner.event.domain.enums.InvitationStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventInvitation {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long inviteeId;
    private final Long eventId;
    @Builder.Default
    private final InvitationStatus status = InvitationStatus.PENDING;
    private final String message;
    private final Instant respondedAt;

    public static EventInvitation create(Long inviteeId, Long eventId, String message) {
        return EventInvitation.builder()
                .inviteeId(inviteeId)
                .eventId(eventId)
                .message(message)
                .build();
    }
}
