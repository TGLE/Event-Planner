package com.tgle.planner.event.factory;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.infrastructure.persistence.entity.EventDatesEmbeddable;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.user.infrastructure.persistence.UserEntity;

import java.time.Duration;

public final class EventTestFactory {

    private EventTestFactory() {}

    public static EventEntity createCancelled(Long publicId, UserEntity organizer) {
        return cancelledBuilder(publicId, organizer).build();
    }

    public static EventEntity createCancelledPublic(Long publicId, UserEntity organizer) {
        return cancelledPublicBuilder(publicId, organizer).build();
    }

    public static EventEntity createDeletedPublishedPublic(Long publicId, UserEntity organizer) {
        return deletedPublishedPublicBuilder(publicId, organizer).build();
    }

    public static EventEntity createDeleted(Long publicId, UserEntity organizer) {
        return deletedBuilder(publicId, organizer).build();
    }

    public static EventEntity createFinishedPublic(Long publicId, UserEntity organizer) {
        return finishedPublicBuilder(publicId, organizer).build();
    }

    public static EventEntity createFinished(Long publicId, UserEntity organizer) {
        return finishedBuilder(publicId, organizer).build();
    }

    public static EventEntity createPublishedPrivate(Long publicId, UserEntity organizer) {
        return publishedBuilder(publicId, organizer).build();
    }

    public static EventEntity createPublishedPublic(Long publicId, UserEntity organizer) {
        return publishedPublicBuilder(publicId, organizer).build();
    }

    public static EventEntity createDraftPublic(Long publicId, UserEntity organizer) {
        return defaultPublicBuilder(publicId, organizer).build();
    }

    public static EventEntity createDefault(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer).build();
    }

    public static EventEntity.EventEntityBuilder cancelledPublicBuilder(Long publicId, UserEntity organizer) {
        return cancelledBuilder(publicId, organizer)
                .visibility(EventVisibility.PUBLIC);
    }

    public static EventEntity.EventEntityBuilder cancelledBuilder(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer)
                .status(EventStatus.CANCELLED)
                .dates(EventDatesEmbeddable.builder()
                        .publishedAt(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(2)))
                        .cancelledAt(TimeTestConfig.TEST_NOW)
                        .build());
    }

    public static EventEntity.EventEntityBuilder finishedPublicBuilder(Long publicId, UserEntity organizer) {
        return finishedBuilder(publicId, organizer)
                .visibility(EventVisibility.PUBLIC);
    }

    public static EventEntity.EventEntityBuilder finishedBuilder(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer)
                .status(EventStatus.FINISHED)
                .dates(EventDatesEmbeddable.builder()
                        .publishedAt(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(2)))
                        .finishedAt(TimeTestConfig.TEST_NOW)
                        .build());
    }

    public static EventEntity.EventEntityBuilder deletedPublishedPublicBuilder(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer)
                .visibility(EventVisibility.PUBLIC)
                .status(EventStatus.PUBLISHED)
                .state(EventState.DELETED)
                .dates(EventDatesEmbeddable.builder()
                        .publishedAt(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(2)))
                        .deletedAt(TimeTestConfig.TEST_NOW)
                        .build());
    }

    public static EventEntity.EventEntityBuilder deletedBuilder(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer)
                .state(EventState.DELETED)
                .dates(EventDatesEmbeddable.builder()
                        .deletedAt(TimeTestConfig.TEST_NOW)
                        .build());
    }

    public static EventEntity.EventEntityBuilder publishedPublicBuilder(Long publicId, UserEntity organizer) {
        return publishedBuilder(publicId, organizer)
                .visibility(EventVisibility.PUBLIC);
    }

    public static EventEntity.EventEntityBuilder publishedBuilder(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer)
                .status(EventStatus.PUBLISHED)
                .dates(EventDatesEmbeddable.builder()
                        .publishedAt(TimeTestConfig.TEST_NOW)
                        .build());
    }

    public static EventEntity.EventEntityBuilder defaultPublicBuilder(Long publicId, UserEntity organizer) {
        return defaultBuilder(publicId, organizer)
                .visibility(EventVisibility.PUBLIC);
    }

    public static EventEntity.EventEntityBuilder defaultBuilder(Long publicId, UserEntity organizer) {
        return EventEntity.builder()
                .publicId(publicId)
                .title("Test Event")
                .location("Test location")
                .description("This is a test event")
                .startTime(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(1)))
                .endTime(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(2)))
                .visibility(EventVisibility.PRIVATE)
                .entryType(EventEntryType.FREE)
                .status(EventStatus.DRAFT)
                .state(EventState.ACTIVE)
                .organizer(organizer)
                .dates(EventDatesEmbeddable.builder().build());
    }
}
