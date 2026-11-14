package com.tgle.planner.event.infrastructure.persistence.specifications;

import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class EventSpecifications {
    private EventSpecifications() {}

    public static Specification<EventEntity> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String pattern = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("location")), pattern)
            );
        };
    }

    public static Specification<EventEntity> startsAfter(Instant startsAfter) {
        return (root, query, cb) ->
                startsAfter == null ? null : cb.greaterThanOrEqualTo(root.get("startTime"), startsAfter);
    }

    public static Specification<EventEntity> startsBefore(Instant startsBefore) {
        return (root, query, cb) ->
                startsBefore == null ? null : cb.lessThanOrEqualTo(root.get("startTime"), startsBefore);
    }

    public static Specification<EventEntity> minCapacity(Integer minCapacity) {
        return (root, query, cb) ->
                minCapacity == null ? null : cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
    }

    public static Specification<EventEntity> maxCapacity(Integer maxCapacity) {
        return (root, query, cb) ->
                maxCapacity == null ? null : cb.lessThanOrEqualTo(root.get("capacity"), maxCapacity);
    }

    public static Specification<EventEntity> hasVisibility(EventVisibility visibility) {
        return (root, query, cb) ->
                visibility == null ? null : cb.equal(root.get("visibility"), visibility);
    }

    public static Specification<EventEntity> hasEntryType(EventEntryType entryType) {
        return (root, query, cb) ->
                entryType == null ? null : cb.equal(root.get("entryType"), entryType);
    }

    public static Specification<EventEntity> hasStatus(EventStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<EventEntity> hasState(EventState state) {
        return (root, query, cb) ->
                state == null ? null : cb.equal(root.get("state"), state);
    }

    public static Specification<EventEntity> notDeleted() {
        return (root, query, cb) ->
                cb.notEqual(root.get("state"), EventState.DELETED);
    }

    public static Specification<EventEntity> hasOrganizer(Long organizerId) {
        return (root, query, cb) ->
                cb.equal(root.get("organizer").get("id"), organizerId);
    }
}
