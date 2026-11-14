package com.tgle.planner.event.presentation.dto.response;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventStatus;

import java.time.Instant;

public record PublicEventResponse(
        Long publicId,
        String title,
        String location,
        String description,
        String imageUrl,
        Instant startTime,
        Instant endTime,
        Integer capacity,
        EventEntryType entryType,
        EventStatus status,
        PublicEventDatesResponse dates
) {
    public record PublicEventDatesResponse(
            Instant publishedAt,
            Instant finishedAt,
            Instant cancelledAt
    ) {
    }
}
