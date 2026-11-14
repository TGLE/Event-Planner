package com.tgle.planner.event.domain.repository;

import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.infrastructure.persistence.projections.EventSummary;
import com.tgle.planner.event.infrastructure.persistence.projections.PublicEventSummary;
import com.tgle.planner.event.presentation.search.EventSearchCriteria;
import com.tgle.planner.event.presentation.search.PublicEventSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<Event> findById(Long id);
    Optional<Event> findByPublicId(Long publicId);
    Page<EventSummary> findOrganizerEvents(Long organizerId, EventSearchCriteria criteria, Pageable pageable);
    Page<PublicEventSummary> findPublicEvents(PublicEventSearchCriteria criteria, Pageable pageable);
    Event save(Event event);
    List<Event> saveAll(List<Event> events);
}
