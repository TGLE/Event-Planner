package com.tgle.planner.event.presentation.dto.response;

import com.tgle.planner.event.domain.enums.EventEntryType;

import java.time.Instant;

public record PublicEventSummaryResponse(
        Long publicId,
        String title,
        String location,
        String imageUrl,
        Instant startTime,
        Instant endTime,
        EventEntryType entryType,
        Instant publishedAt
) {
}
