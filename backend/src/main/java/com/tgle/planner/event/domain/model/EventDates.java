package com.tgle.planner.event.domain.model;

import com.tgle.planner.core.errorhandling.exception.IllegalEventStateException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class EventDates {
    private final Instant publishedAt;
    private final Instant finishedAt;
    private final Instant cancelledAt;
    private final Instant deletedAt;

    public static EventDates none() {
        return EventDates.builder().build();
    }

    public EventDates publish(Instant now) {
        if (publishedAt != null) {
            throw new IllegalEventStateException("Event is already published");
        }
        return toBuilder()
                .publishedAt(now)
                .build();
    }

    public EventDates finish(Instant now) {
        if (finishedAt != null) {
            throw new IllegalEventStateException("Event is already finished");
        }
        return toBuilder()
                .finishedAt(now)
                .build();
    }

    public EventDates cancel(Instant now) {
        if (cancelledAt != null) {
            throw new IllegalEventStateException("Event is already cancelled");
        }
        return toBuilder()
                .cancelledAt(now)
                .build();
    }

    public EventDates restore() {
        if (deletedAt == null) {
            throw new IllegalEventStateException("Event is not deleted");
        }
        return toBuilder()
                .deletedAt(null)
                .build();
    }

    public EventDates delete(Instant now) {
        if (deletedAt != null) {
            throw new IllegalEventStateException("Event is already deleted");
        }
        return toBuilder()
                .deletedAt(now)
                .build();
    }
}
