package com.tgle.planner.event.application.dto.command;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventVisibility;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.Instant;

public record EventUpdateCommand(
        String title,
        JsonNullable<String> location,
        JsonNullable<String> description,
        JsonNullable<Instant> startTime,
        JsonNullable<Instant> endTime,
        JsonNullable<Integer> capacity,
        JsonNullable<EventVisibility> visibility,
        JsonNullable<EventEntryType> entryType,
        Long publicId,
        Long userId
) {
}
