package com.tgle.planner.event.presentation.search;

import com.tgle.planner.event.domain.enums.EventEntryType;

import java.time.Instant;

public record PublicEventSearchCriteria(
        String search,
        String location,
        Instant startsAfter,
        Instant startsBefore,
        Integer minCapacity,
        Integer maxCapacity,
        EventEntryType entryType,
        EventSortOption sortOption
) {
}
