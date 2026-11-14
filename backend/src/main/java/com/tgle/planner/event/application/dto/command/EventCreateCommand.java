package com.tgle.planner.event.application.dto.command;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventVisibility;

import java.time.Instant;

public record EventCreateCommand(
        String title,
        String location,
        String description,
        Instant startTime,
        Instant endTime,
        Integer capacity,
        EventVisibility visibility,
        EventEntryType entryType,
        Long userId
) {
}
