package com.tgle.planner.event.domain.model;

import com.tgle.planner.event.domain.enums.ParticipantStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventParticipant {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long userId;
    private final Long eventId;
    private final Long ticketTypeId;
    private final ParticipantStatus status;
    private final String checkInToken;
    private final Instant checkInAt;

    public static EventParticipant create(
            Long userId,
            Long eventId,
            Long ticketTypeId,
            ParticipantStatus status,
            String checkInToken
    ) {
        return EventParticipant.builder()
                .userId(userId)
                .eventId(eventId)
                .ticketTypeId(ticketTypeId)
                .status(status)
                .checkInToken(checkInToken)
                .build();
    }
}
