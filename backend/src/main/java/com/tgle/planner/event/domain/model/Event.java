package com.tgle.planner.event.domain.model;

import com.tgle.planner.core.errorhandling.exception.EventNotViewableException;
import com.tgle.planner.core.errorhandling.exception.IllegalEventStateException;
import com.tgle.planner.core.errorhandling.exception.InvalidEventException;
import com.tgle.planner.event.domain.EventAccess;
import com.tgle.planner.event.domain.EventValidator;
import com.tgle.planner.event.domain.data.EventCreateData;
import com.tgle.planner.event.domain.data.EventUpdateData;
import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long publicId;
    private final String title;
    private final String location;
    private final String description;
    private final String imagePath;
    private final Instant startTime;
    private final Instant endTime;
    private final Capacity capacity;
    private final EventVisibility visibility;
    private final EventEntryType entryType;
    private final EventStatus status;
    private final EventState state;
    private final Long organizerId;
    private final EventDates dates;

    @Builder(toBuilder = true)
    private Event(Long id, Long publicId, String title, String location, String description, String imagePath,
                  Instant startTime, Instant endTime, Capacity capacity, EventVisibility visibility,
                  EventEntryType entryType, EventStatus status, EventState state, Long organizerId,
                  EventDates dates) {

        EventValidator.validateTitle(title);
        EventValidator.validateLocation(location);
        EventValidator.validateDescription(description);
        EventValidator.validateImagePath(imagePath);
        EventValidator.validateTimeRange(startTime, endTime);
        EventValidator.validateOrganizerId(organizerId);
        EventValidator.validatePublicId(publicId);

        this.id = id;
        this.publicId = publicId;
        this.title = title;
        this.location = location;
        this.description = description;
        this.imagePath = imagePath;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity != null ? capacity : Capacity.unlimited();
        this.visibility = visibility != null ? visibility : EventVisibility.PRIVATE;
        this.entryType = entryType != null ? entryType : EventEntryType.FREE;
        this.status = status != null ? status : EventStatus.DRAFT;
        this.state = state != null ? state : EventState.ACTIVE;
        this.organizerId = organizerId;
        this.dates = dates != null ? dates : EventDates.none();
    }

    public static Event create(EventCreateData data, Long publicId, Long organizerId) {
        return Event.builder()
                .publicId(publicId)
                .title(data.title())
                .location(data.location())
                .description(data.description())
                .startTime(data.startTime())
                .endTime(data.endTime())
                .capacity(data.capacity())
                .visibility(data.visibility())
                .entryType(data.entryType())
                .organizerId(organizerId)
                .build();
    }

    public Event update(EventUpdateData data) {
        EventBuilder builder = toBuilder();
        if (data.title() != null) {
            builder.title(data.title());
        }
        data.location().ifPresent(builder::location);
        data.description().ifPresent(builder::description);
        data.startTime().ifPresent(builder::startTime);
        data.endTime().ifPresent(builder::endTime);
        data.capacity().ifPresent(builder::capacity);
        data.visibility().ifPresent(builder::visibility);
        data.entryType().ifPresent(builder::entryType);
        return builder.build();
    }

    public void ensureCanUpdate(EventUpdateData data, Instant now) {
        if (isDeleted()) {
            throw new IllegalEventStateException("Cannot update an event that is deleted");
        }
        if (isFinished()) {
            throw new IllegalEventStateException("Cannot update an event that is finished");
        }
        if (isCancelled()) {
            ensureOnlyDescriptionChanges(data);
            return;
        }
        List<String> errors = isPublished()
                ? validatePublishedUpdateRestrictions(data)
                : validateDraftUpdateRestrictions(data, now);

        if (!errors.isEmpty()) {
            throw new InvalidEventException(errors);
        }
    }

    public Event publish(Instant now) {
        if (isDeleted()) {
            throw new IllegalEventStateException("Cannot publish an event that is deleted");
        }
        if (isPublished()) {
            throw new IllegalEventStateException("The event is already published");
        }
        if (!isDraft()) {
            throw new IllegalEventStateException("Only draft events can be published");
        }
        List<String> errors = validatePublishRequirements(now);
        if (!errors.isEmpty()) {
            throw new InvalidEventException(errors);
        }
        return toBuilder()
                .status(EventStatus.PUBLISHED)
                .dates(dates.publish(now))
                .build();
    }

    public Event cancel(Instant now) {
        if (isDeleted()) {
            throw new IllegalEventStateException("Cannot cancel an event that is deleted");
        }
        if (isCancelled()) {
            throw new IllegalEventStateException("The event is already cancelled");
        }
        if (!isPublished()) {
            throw new IllegalEventStateException("Only published events can be cancelled");
        }
        return toBuilder()
                .status(EventStatus.CANCELLED)
                .dates(dates.cancel(now))
                .build();
    }

    public Event restore() {
        if (!isDeleted()) {
            throw new IllegalEventStateException("Only deleted events can be restored");
        }
        return toBuilder()
                .state(EventState.ACTIVE)
                .dates(dates.restore())
                .build();
    }

    public Event delete(Instant now) {
        if (isDeleted()) {
            throw new IllegalEventStateException("The event is already deleted");
        }
        return toBuilder()
                .state(EventState.DELETED)
                .dates(dates.delete(now))
                .build();
    }

    public Event updateImage(String imagePath) {
        return this.toBuilder()
                .imagePath(imagePath)
                .build();
    }

    public Event deleteImage() {
        return this.toBuilder()
                .imagePath(null)
                .build();
    }

    public EventAccess resolveAccessFor(Long userId) {
        boolean owner = userId != null && isOwner(userId);

        if (!owner && !isPubliclyVisible()) {
            throw new EventNotViewableException("Event could not be found");
        }
        return new EventAccess(owner);
    }

    public void ensureModifiableBy(Long userId) {
        if (isOwner(userId)) {
            return;
        }
        if (!isPubliclyVisible()) {
            throw new EventNotViewableException("Event could not be found");
        }
        throw new AccessDeniedException("Invalid organizer cannot access this resource");
    }

    public void ensureCanUpdateImage() {
        if (isDeleted()) {
            throw new IllegalEventStateException("Cannot update the image of an event that is deleted");
        }
        if (isFinished() || isCancelled()) {
            throw new IllegalEventStateException(
                    "Cannot update the image of an event that is " + status.name().toLowerCase()
            );
        }
    }

    public boolean isOwner(Long userId) {
        return organizerId.equals(userId);
    }

    public boolean isPubliclyVisible() {
        return isActive() && isPublic() && !isDraft();
    }

    public boolean isPublic() {
        return visibility == EventVisibility.PUBLIC;
    }

    public boolean isPrivate() {
        return visibility == EventVisibility.PRIVATE;
    }

    public boolean isDraft() {
        return status == EventStatus.DRAFT;
    }

    public boolean isPublished() {
        return status == EventStatus.PUBLISHED;
    }

    public boolean isFinished() {
        return status == EventStatus.FINISHED;
    }

    public boolean isCancelled() {
        return status == EventStatus.CANCELLED;
    }

    public boolean isActive() {
        return state == EventState.ACTIVE;
    }

    public boolean isDeleted() {
        return state == EventState.DELETED;
    }

    private void ensureOnlyDescriptionChanges(EventUpdateData data) {
        if (hasChangesOtherThanDescription(data)) {
            throw new IllegalEventStateException("Only the description can be changed for cancelled events");
        }
    }

    private boolean hasChangesOtherThanDescription(EventUpdateData data) {
        return data.title() != null || data.location().isPresent() || data.startTime().isPresent()
                || data.endTime().isPresent() || data.capacity().isPresent()
                || data.visibility().isPresent() || data.entryType().isPresent();
    }

    private List<String> validateDraftUpdateRestrictions(EventUpdateData data, Instant now) {
        List<String> errors = new ArrayList<>();
        if (data.startTime().isPresent()) {
            EventValidator.checkNotFutureStartTime(data.startTime().orElse(null), now)
                    .ifPresent(errors::add);
        }
        return errors;
    }

    private List<String> validatePublishedUpdateRestrictions(EventUpdateData data) {
        List<String> errors = new ArrayList<>();
        if (data.location().isPresent() && data.location().orElse(null) == null) {
            errors.add("Location cannot be cleared for a published event");
        }
        if (data.startTime().isPresent()) {
            errors.add("Cannot change start time on a published event");
        }
        if (data.endTime().isPresent()) {
            errors.add("Cannot change end time on a published event");
        }
        if (data.entryType().isPresent()) {
            EventValidator.checkEntryTypeChange(entryType, data.entryType().orElse(null))
                    .ifPresent(errors::add);
        }
        return errors;
    }

    private List<String> validatePublishRequirements(Instant now) {
        List<String> errors = new ArrayList<>();
        if (title == null) {
            errors.add("Title is required");
        }
        if (location == null) {
            errors.add("Location is required");
        }
        if (startTime == null) {
            errors.add("Start time is required");
        } else if (!startTime.isAfter(now)) {
            errors.add("Start time must be in the future");
        }
        if (endTime == null) {
            errors.add("End time is required");
        }
        if (visibility == null) {
            errors.add("Visibility is required");
        }
        if (entryType == null) {
            errors.add("Entry type is required");
        }
        return errors;
    }
}
