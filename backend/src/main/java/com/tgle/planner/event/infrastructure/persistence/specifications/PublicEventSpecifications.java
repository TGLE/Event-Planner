package com.tgle.planner.event.infrastructure.persistence.specifications;

import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.presentation.search.PublicEventSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public final class PublicEventSpecifications {
    private PublicEventSpecifications() {}

    public static Specification<EventEntity> filter(PublicEventSearchCriteria criteria) {
        return Specification
                .where(isPublic())
                .and(isPublished())
                .and(isActive())
                .and(EventSpecifications.matchesSearch(criteria.search()))
                .and(EventSpecifications.startsAfter(criteria.startsAfter()))
                .and(EventSpecifications.startsBefore(criteria.startsBefore()))
                .and(EventSpecifications.minCapacity(criteria.minCapacity()))
                .and(EventSpecifications.maxCapacity(criteria.maxCapacity()))
                .and(EventSpecifications.hasEntryType(criteria.entryType()));
    }

    public static Specification<EventEntity> isPublic() {
        return (root, query, cb) ->
                cb.equal(root.get("visibility"), EventVisibility.PUBLIC);
    }

    public static Specification<EventEntity> isPublished() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), EventStatus.PUBLISHED);
    }

    public static Specification<EventEntity> isActive() {
        return (root, query, cb) ->
                cb.equal(root.get("state"), EventState.ACTIVE);
    }
}
