package com.tgle.planner.event.domain.data;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.domain.model.Capacity;

import java.time.Instant;

public record EventCreateData(
        String title,
        String location,
        String description,
        Instant startTime,
        Instant endTime,
        Capacity capacity,
        EventVisibility visibility,
        EventEntryType entryType
) {
}
