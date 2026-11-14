package com.tgle.planner.event.infrastructure.persistence.projections;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.infrastructure.persistence.entity.EventDatesEmbeddable;

import java.time.Instant;

public record PublicEventSummary(
        Long publicId,
        String title,
        String location,
        String imagePath,
        Instant startTime,
        Instant endTime,
        EventEntryType entryType,
        EventDatesEmbeddable dates
) {
}
