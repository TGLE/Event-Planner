package com.tgle.planner.event.presentation.search;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;

import java.time.Instant;

public record EventSearchCriteria(
        String search,
        Instant startsAfter,
        Instant startsBefore,
        Integer minCapacity,
        Integer maxCapacity,
        EventVisibility visibility,
        EventEntryType entryType,
        EventStatus status,
        EventState state,
        EventSortOption sortOption
) {
}
