package com.tgle.planner.event.presentation.dto.response;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;

import java.time.Instant;

public record EventSummaryResponse(
        Long publicId,
        String title,
        String location,
        String imageUrl,
        Instant startTime,
        Instant endTime,
        EventVisibility visibility,
        EventEntryType entryType,
        EventStatus status,
        EventState state,
        Instant createdAt
) {
}
