package com.tgle.planner.event.domain.data;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.domain.model.Capacity;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.Instant;

public record EventUpdateData(
        String title,
        JsonNullable<String> location,
        JsonNullable<String> description,
        JsonNullable<Instant> startTime,
        JsonNullable<Instant> endTime,
        JsonNullable<Capacity> capacity,
        JsonNullable<EventVisibility> visibility,
        JsonNullable<EventEntryType> entryType
) {
}
