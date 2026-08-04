package com.tgle.planner.event.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventBookmark {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long userId;
    private final Long eventId;

    public static EventBookmark create(
            Long userId,
            Long eventId
    ) {
        return EventBookmark.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
    }
}
