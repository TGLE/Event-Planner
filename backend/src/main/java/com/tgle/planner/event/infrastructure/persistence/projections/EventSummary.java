package com.tgle.planner.event.infrastructure.persistence.projections;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;

import java.time.Instant;

public record EventSummary(
        Long publicId,
        String title,
        String location,
        String imagePath,
        Instant startTime,
        Instant endTime,
        EventVisibility visibility,
        EventEntryType entryType,
        EventStatus status,
        EventState state,
        Instant createdAt
) {
}
