package com.tgle.planner.event.presentation.dto.response;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;

import java.time.Instant;

public record EventResponse(
        Long publicId,
        String title,
        String location,
        String description,
        String imageUrl,
        Instant startTime,
        Instant endTime,
        Integer capacity,
        EventVisibility visibility,
        EventEntryType entryType,
        EventStatus status,
        EventState state,
        EventDatesResponse dates
) {
    public record EventDatesResponse(
            Instant publishedAt,
            Instant finishedAt,
            Instant cancelledAt,
            Instant deletedAt
    ) {
    }
}
