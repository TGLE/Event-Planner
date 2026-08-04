package com.tgle.planner.event.domain.model;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {
    @EqualsAndHashCode.Include
    private final Long id;
    private final String title;
    private final String description;
    private final Instant startTime;
    private final Instant endTime;
    private final EventVisibility visibility;
    @Builder.Default
    private final EventStatus status = EventStatus.DRAFT;
    @Builder.Default
    private final EventState state = EventState.ACTIVE;
    private final EventEntryType entryType;
    private final Long organizerId;
    private final Integer capacity;
    private final Instant publishedAt;
    private final Instant deletedAt;
    private final String slug;

    public static Event create(
            String title,
            String description,
            Instant startTime,
            Instant endTime,
            EventVisibility visibility,
            EventEntryType entryType,
            Long organizerId,
            Integer capacity
    ) {
        return Event.builder()
                .title(title)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .visibility(visibility)
                .entryType(entryType)
                .organizerId(organizerId)
                .capacity(capacity)
                .build();
    }
}
