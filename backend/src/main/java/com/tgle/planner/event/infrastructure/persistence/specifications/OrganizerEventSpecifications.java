package com.tgle.planner.event.infrastructure.persistence.specifications;

import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.presentation.search.EventSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public final class OrganizerEventSpecifications {
    private OrganizerEventSpecifications() {}

    public static Specification<EventEntity> filter(EventSearchCriteria criteria, Long organizerId) {
        return Specification
                .where(EventSpecifications.hasOrganizer(organizerId))
                .and(EventSpecifications.matchesSearch(criteria.search()))
                .and(EventSpecifications.matchesLocation(criteria.location()))
                .and(EventSpecifications.startsAfter(criteria.startsAfter()))
                .and(EventSpecifications.startsBefore(criteria.startsBefore()))
                .and(EventSpecifications.minCapacity(criteria.minCapacity()))
                .and(EventSpecifications.maxCapacity(criteria.maxCapacity()))
                .and(EventSpecifications.hasVisibility(criteria.visibility()))
                .and(EventSpecifications.hasEntryType(criteria.entryType()))
                .and(EventSpecifications.hasStatus(criteria.status()))
                .and(getStateSpecification(criteria.state()));
    }

    private static Specification<EventEntity> getStateSpecification(EventState state) {
        return state == null ? EventSpecifications.notDeleted() : EventSpecifications.hasState(state);
    }
}
