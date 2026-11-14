package com.tgle.planner.event;

import com.tgle.planner.event.domain.model.Event;

public record EventView(
        Event event,
        boolean owner
) {
}
