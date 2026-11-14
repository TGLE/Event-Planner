package com.tgle.planner.event.application.service;

import com.tgle.planner.core.errorhandling.exception.ImageProcessingException;
import com.tgle.planner.event.domain.EventValidator;
import com.tgle.planner.event.domain.SnowflakeIdGenerator;
import com.tgle.planner.event.domain.data.EventCreateData;
import com.tgle.planner.event.domain.data.EventUpdateData;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.domain.repository.EventRepository;
import com.tgle.planner.image.application.ImageService;
import com.tgle.planner.image.application.ImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventManagementService {

    private final Clock clock;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final ImageService imageService;
    private final ImageValidator imageValidator;
    private final EventRepository eventRepository;
    private final EventParticipantService eventParticipantService;

    @Transactional
    public Event createEvent(EventCreateData data, Long organizerId) {
        Long publicId = snowflakeIdGenerator.nextId();
        EventValidator.validateFutureStartTime(data.startTime(), Instant.now(clock));
        Event event = Event.create(data, publicId, organizerId);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Event event, EventUpdateData data) {
        event.ensureCanUpdate(data, Instant.now(clock));
        validatePublishedEventCapacity(event, data);
        Event updatedEvent = event.update(data);
        return eventRepository.save(updatedEvent);
    }

    @Transactional
    public Event publishEvent(Event event) {
        Event publishedEvent = event.publish(Instant.now(clock));
        return eventRepository.save(publishedEvent);
    }

    @Transactional
    public Event cancelEvent(Event event) {
        Event cancelledEvent = event.cancel(Instant.now(clock));
        return eventRepository.save(cancelledEvent);
    }

    @Transactional
    public Event restoreEvent(Event event) {
        Event restoredEvent = event.restore();
        return eventRepository.save(restoredEvent);
    }

    @Transactional
    public Event deleteEvent(Event event) {
        Event deletedEvent = event.delete(Instant.now(clock));
        return eventRepository.save(deletedEvent);
    }

    @Transactional
    public void updateEventImage(Event event, MultipartFile image, Long publicId) {
        imageValidator.validateEventImage(image);
        String oldImagePath = event.getImagePath();
        Event updatedEvent;
        try (InputStream inputStream = image.getInputStream()) {
            String newImagePath = imageService.storeEventImage("events/" + publicId, inputStream);
            updatedEvent = event.updateImage(newImagePath);
        } catch (IOException e) {
            throw new ImageProcessingException("Could not process event image", e);
        }
        eventRepository.save(updatedEvent);
        imageService.deleteImage(oldImagePath);
    }

    @Transactional(readOnly = true)
    public Resource getEventImage(Event event) {
        return imageService.loadImage(event.getImagePath());
    }

    private void validatePublishedEventCapacity(Event event, EventUpdateData data) {
        if (event.isPublished()) {
            data.capacity().ifPresent(capacity -> {
                long participantCount = eventParticipantService.countByEventId(event.getId());
                EventValidator.validateCapacityChange(capacity, participantCount);
            });
        }
    }
}
