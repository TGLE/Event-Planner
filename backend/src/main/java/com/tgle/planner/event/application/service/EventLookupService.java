package com.tgle.planner.event.application.service;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.domain.repository.EventRepository;
import com.tgle.planner.event.infrastructure.persistence.projections.EventSummary;
import com.tgle.planner.event.infrastructure.persistence.projections.PublicEventSummary;
import com.tgle.planner.event.presentation.search.EventSearchCriteria;
import com.tgle.planner.event.presentation.search.EventSortOption;
import com.tgle.planner.event.presentation.search.PublicEventSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventLookupService {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event"));
    }

    @Transactional(readOnly = true)
    public Event findByPublicId(Long publicId) {
        return eventRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event"));
    }

    @Transactional(readOnly = true)
    public Page<EventSummary> findOrganizerEvents(Long organizerId, EventSearchCriteria criteria, Pageable pageable) {
        Sort sort = getOrganizerSort(criteria.sortOption());

        return eventRepository.findOrganizerEvents(organizerId, criteria, getSortedPageable(pageable, sort));
    }

    @Transactional(readOnly = true)
    public Page<PublicEventSummary> findPublicEvents(PublicEventSearchCriteria criteria, Pageable pageable) {
        Sort sort = getPublicSort(criteria.sortOption());

        return eventRepository.findPublicEvents(criteria, getSortedPageable(pageable, sort));
    }

    private Pageable getSortedPageable(Pageable pageable, Sort sort) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    private Sort getPublicSort(EventSortOption sortOption) {
        return getSort(sortOption, "dates.publishedAt");
    }

    private Sort getOrganizerSort(EventSortOption sortOption) {
        return getSort(sortOption, "createdAt");
    }

    private Sort getSort(EventSortOption sortOption, String property) {
        EventSortOption sort = sortOption != null ? sortOption : EventSortOption.NEWEST;
        return switch (sort) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, property);
            case OLDEST -> Sort.by(Sort.Direction.ASC, property);
        };
    }
}
