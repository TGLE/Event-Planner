package com.tgle.planner.event.presentation;

import com.tgle.planner.event.EventView;
import com.tgle.planner.event.application.EventService;
import com.tgle.planner.event.application.dto.command.EventCreateCommand;
import com.tgle.planner.event.application.dto.command.EventImageUploadCommand;
import com.tgle.planner.event.application.dto.command.EventUpdateCommand;
import com.tgle.planner.event.application.dto.result.EventResult;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.infrastructure.persistence.projections.EventSummary;
import com.tgle.planner.event.infrastructure.persistence.projections.PublicEventSummary;
import com.tgle.planner.event.presentation.dto.request.*;
import com.tgle.planner.event.presentation.dto.response.EventResponse;
import com.tgle.planner.event.presentation.dto.response.EventSummaryResponse;
import com.tgle.planner.event.presentation.dto.response.PublicEventSummaryResponse;
import com.tgle.planner.event.presentation.search.EventSearchCriteria;
import com.tgle.planner.event.presentation.search.PublicEventSearchCriteria;
import com.tgle.planner.user.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper mapper;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventCreateCommand command = mapper.toCommand(request, userPrincipal.getId());
        EventResult result = eventService.createDraftEvent(command);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<EventResponse> updateEvent(
            @Valid @RequestBody EventUpdateRequest request,
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventUpdateCommand command = mapper.toCommand(request, publicId, userPrincipal.getId());
        EventResult result = eventService.updateEvent(command);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/{publicId}/publish")
    public ResponseEntity<EventResponse> publishEvent(
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventResult result = eventService.publishEvent(publicId, userPrincipal.getId());
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/{publicId}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventResult result = eventService.cancelEvent(publicId, userPrincipal.getId());
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/{publicId}/restore")
    public ResponseEntity<EventResponse> restoreEvent(
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventResult result = eventService.restoreEvent(publicId, userPrincipal.getId());
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<EventResponse> deleteEvent(
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventResult result = eventService.deleteEvent(publicId, userPrincipal.getId());
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<EventSummaryResponse>> getOrganizerEvents(
            @Valid EventSearchRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable
    ) {
        EventSearchCriteria criteria = mapper.toCriteria(request);
        Page<EventSummary> result = eventService.findOrganizerEvents(userPrincipal.getId(), criteria, pageable);
        return ResponseEntity.ok(result.map(mapper::toResponse));
    }

    @GetMapping
    public ResponseEntity<Page<PublicEventSummaryResponse>> getPublicEvents(
            @Valid PublicEventSearchRequest request,
            Pageable pageable
    ) {
        PublicEventSearchCriteria criteria = mapper.toCriteria(request);
        Page<PublicEventSummary> result = eventService.findPublicEvents(criteria, pageable);
        return ResponseEntity.ok(result.map(mapper::toResponse));
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<?> getEvent(
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventView eventView = eventService.findEvent(publicId, userPrincipal);
        Event event = eventView.event();
        return ResponseEntity.ok(eventView.owner() ? mapper.toResponse(event) : mapper.toPublicResponse(event));
    }

    @PatchMapping("/{publicId}/image")
    public ResponseEntity<Void> updateEventImage(
            @PathVariable Long publicId,
            @Valid @ModelAttribute EventImageUploadRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        EventImageUploadCommand command = mapper.toCommand(request, publicId, userPrincipal.getId());
        eventService.updateEventImage(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{publicId}/image")
    public ResponseEntity<Resource> getEventImage(
            @PathVariable Long publicId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Resource image = eventService.getEventImage(publicId, userPrincipal);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}
