package com.tgle.planner.event.presentation.dto.request;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.presentation.search.EventSortOption;
import com.tgle.planner.event.presentation.validator.EventCapacityRange;
import com.tgle.planner.event.presentation.validator.EventTimeSearchRange;
import com.tgle.planner.event.presentation.validator.NullOrNotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@EventTimeSearchRange
@EventCapacityRange
public record PublicEventSearchRequest(
        @Size(max = 100)
        @NullOrNotBlank
        String search,
        Instant startsAfter,
        Instant startsBefore,
        @PositiveOrZero
        Integer minCapacity,
        @PositiveOrZero
        Integer maxCapacity,
        EventEntryType entryType,
        EventSortOption sortOption
) {
}
