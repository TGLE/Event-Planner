package com.tgle.planner.event.presentation.dto.request;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.presentation.validator.EventTimeRange;
import com.tgle.planner.event.presentation.validator.NullOrNotBlank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@EventTimeRange
public record EventCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
        String title,

        @NullOrNotBlank
        @Size(min = 2, max = 255, message = "Location must be between 2 and 255 characters")
        String location,

        @NullOrNotBlank
        @Size(max = 5000, message = "Description can't exceed maximum length of 5000 characters")
        String description,

        Instant startTime,

        Instant endTime,

        @Positive(message = "Capacity must be greater than 0")
        Integer capacity,

        EventVisibility visibility,

        EventEntryType entryType
) {
}
