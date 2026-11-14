package com.tgle.planner.event.application.dto.result;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;

import java.time.Instant;

public record EventResult(
        Long publicId,
        String title,
        String location,
        String description,
        String imagePath,
        Instant startTime,
        Instant endTime,
        Integer capacity,
        EventVisibility visibility,
        EventEntryType entryType,
        EventStatus status,
        EventState state,
        EventDatesResult dates
) {
    public record EventDatesResult(
            Instant publishedAt,
            Instant finishedAt,
            Instant cancelledAt,
            Instant deletedAt
    ) {
    }
}
