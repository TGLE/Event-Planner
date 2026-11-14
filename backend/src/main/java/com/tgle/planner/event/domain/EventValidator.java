package com.tgle.planner.event.domain;

import com.tgle.planner.core.errorhandling.exception.InvalidEventException;
import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.model.Capacity;

import java.time.Instant;
import java.util.Optional;

public final class EventValidator {
    private EventValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void validateTitle(String title) {
        if (title == null) {
            throw new InvalidEventException("Title is required");
        }
        if (title.isBlank()) {
            throw new InvalidEventException("Title can't be blank");
        }
        if (title.length() > 100) {
            throw new InvalidEventException("Title can't exceed maximum length of 100 characters");
        }
    }

    public static void validateLocation(String location) {
        if (location == null) {
            return;
        }
        if (location.isBlank()) {
            throw new InvalidEventException("Location can't be blank if provided");
        }
        if (location.length() > 255) {
            throw new InvalidEventException("Location can't exceed maximum length of 255 characters");
        }
    }

    public static void validateDescription(String description) {
        if (description == null) {
            return;
        }
        if (description.isBlank()) {
            throw new InvalidEventException("Description can't be blank if provided");
        }
        if (description.length() > 5000) {
            throw new InvalidEventException("Description can't exceed maximum length of 5000 characters");
        }
    }

    public static void validateImagePath(String imagePath) {
        if (imagePath == null) {
            return;
        }
        if (imagePath.isBlank()) {
            throw new InvalidEventException("Image path can't be blank if provided");
        }
    }

    public static void validateTimeRange(Instant startTime, Instant endTime) {
        if (startTime == null || endTime == null) {
            return;
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidEventException("End time must be after start time");
        }
    }

    public static void validateOrganizerId(Long organizerId) {
        if (organizerId == null) {
            throw new InvalidEventException("Organizer id is required");
        }
    }

    public static void validatePublicId(Long publicId) {
        if (publicId == null) {
            throw new InvalidEventException("Public id is required");
        }
    }

    public static void validateFutureStartTime(Instant startTime, Instant now) {
        if (startTime == null) {
            return;
        }
        if (!startTime.isAfter(now)) {
            throw new InvalidEventException("Start time must be in the future");
        }
    }

    public static void validateCapacityChange(Capacity newCapacity, long participantCount) {
        if (newCapacity.isUnlimited()) {
            return;
        }
        if (newCapacity.value() < participantCount) {
            throw new InvalidEventException("Capacity cannot be lower than the number of registered participants");
        }
    }

    public static Optional<String> checkNotFutureStartTime(Instant startTime, Instant now) {
        if (startTime == null) {
            return Optional.empty();
        }
        if (!startTime.isAfter(now)) {
            return Optional.of("Start time must be in the future");
        }
        return Optional.empty();
    }

    public static Optional<String> checkEntryTypeChange(EventEntryType currentEntryType, EventEntryType newEntryType) {
        if (newEntryType == null) {
            return Optional.empty();
        }
        if (currentEntryType == EventEntryType.FREE && newEntryType == EventEntryType.TICKET_REQUIRED) {
            return Optional.of("Cannot change entry type from free to ticket required on a published event");
        }
        return Optional.empty();
    }
}
