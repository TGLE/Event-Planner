package com.tgle.planner.event.application;

import com.tgle.planner.core.errorhandling.exception.EventNotViewableException;
import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.EventView;
import com.tgle.planner.event.application.dto.command.EventCreateCommand;
import com.tgle.planner.event.application.dto.command.EventImageUploadCommand;
import com.tgle.planner.event.application.dto.command.EventUpdateCommand;
import com.tgle.planner.event.application.dto.result.EventResult;
import com.tgle.planner.event.application.service.EventManagementService;
import com.tgle.planner.event.application.service.EventLookupService;
import com.tgle.planner.event.domain.EventAccess;
import com.tgle.planner.event.domain.data.EventCreateData;
import com.tgle.planner.event.domain.data.EventUpdateData;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.infrastructure.persistence.projections.EventSummary;
import com.tgle.planner.event.infrastructure.persistence.projections.PublicEventSummary;
import com.tgle.planner.event.presentation.EventMapper;
import com.tgle.planner.event.presentation.search.EventSearchCriteria;
import com.tgle.planner.event.presentation.search.PublicEventSearchCriteria;
import com.tgle.planner.user.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper mapper;
    private final EventManagementService eventManagementService;
    private final EventLookupService eventLookupService;

    @Transactional
    public EventResult createDraftEvent(EventCreateCommand command) {
        EventCreateData data = mapper.toData(command);
        Event event = eventManagementService.createEvent(data, command.userId());
        return mapper.toResult(event);
    }

    @Transactional
    public EventResult updateEvent(EventUpdateCommand command) {
        Event event = eventLookupService.findByPublicId(command.publicId());
        event.ensureModifiableBy(command.userId());
        EventUpdateData data = mapper.toData(command);
        Event updatedEvent = eventManagementService.updateEvent(event, data);
        return mapper.toResult(updatedEvent);
    }

    @Transactional
    public EventResult publishEvent(Long publicId, Long userId) {
        Event event = eventLookupService.findByPublicId(publicId);
        event.ensureModifiableBy(userId);
        Event publishedEvent = eventManagementService.publishEvent(event);
        return mapper.toResult(publishedEvent);
    }

    @Transactional
    public EventResult cancelEvent(Long publicId, Long userId) {
        Event event = eventLookupService.findByPublicId(publicId);
        event.ensureModifiableBy(userId);
        Event cancelledEvent = eventManagementService.cancelEvent(event);
        return mapper.toResult(cancelledEvent);
    }

    @Transactional
    public EventResult deleteEvent(Long publicId, Long userId) {
        Event event = eventLookupService.findByPublicId(publicId);
        event.ensureModifiableBy(userId);
        Event deletedEvent = eventManagementService.deleteEvent(event);
        return mapper.toResult(deletedEvent);
    }

    @Transactional
    public EventResult restoreEvent(Long publicId, Long userId) {
        Event event = eventLookupService.findByPublicId(publicId);
        event.ensureModifiableBy(userId);
        Event restoredEvent = eventManagementService.restoreEvent(event);
        return mapper.toResult(restoredEvent);
    }

    @Transactional(readOnly = true)
    public Page<EventSummary> findOrganizerEvents(Long organizerId, EventSearchCriteria criteria, Pageable pageable) {
        return eventLookupService.findOrganizerEvents(organizerId, criteria, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PublicEventSummary> findPublicEvents(PublicEventSearchCriteria criteria, Pageable pageable) {
        return eventLookupService.findPublicEvents(criteria, pageable);
    }

    @Transactional(readOnly = true)
    public EventView findEvent(Long publicId, UserPrincipal userPrincipal) {
        Event event = eventLookupService.findByPublicId(publicId);
        Long callerId = userPrincipal != null ? userPrincipal.getId() : null;

        EventAccess eventAccess = event.resolveAccessFor(callerId);
        return new EventView(event, eventAccess.owner());
    }

    @Transactional
    public void updateEventImage(EventImageUploadCommand command) {
        Event event = eventLookupService.findByPublicId(command.publicId());
        event.ensureModifiableBy(command.userId());
        event.ensureCanUpdateImage();
        eventManagementService.updateEventImage(event, command.image(), command.publicId());
    }

    @Transactional(readOnly = true)
    public Resource getEventImage(Long publicId, UserPrincipal userPrincipal) {
        Event event = eventLookupService.findByPublicId(publicId);
        Long callerId = userPrincipal != null ? userPrincipal.getId() : null;

        event.resolveAccessFor(callerId);
        return eventManagementService.getEventImage(event);
    }
}
