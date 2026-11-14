package com.tgle.planner.event.integrationtest;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.event.presentation.dto.request.EventUpdateRequest;
import com.tgle.planner.event.presentation.dto.response.EventResponse;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class EventUpdateControllerTest extends BaseEventControllerTest {

    private EventEntity event;

    EventUpdateControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Test
    void shouldReturnOkWhenRequestIsValid() {
        event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

        EventUpdateRequest request = new EventUpdateRequest(
                "Updated Event",
                JsonNullable.of("Updated location"),
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined(),
                JsonNullable.of(100),
                JsonNullable.undefined(),
                JsonNullable.of(EventEntryType.TICKET_REQUIRED)
        );

        update(request, accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.publicId()).isEqualTo(1L);
                    assertThat(response.title()).isEqualTo("Updated Event");
                    assertThat(response.location()).isEqualTo("Updated location");
                    assertThat(response.description()).isEqualTo("This is a test event");
                    assertThat(response.startTime()).isEqualTo("2026-08-01T01:00:00Z");
                    assertThat(response.endTime()).isEqualTo("2026-08-01T02:00:00Z");
                    assertThat(response.capacity()).isEqualTo(100);
                    assertThat(response.visibility()).isEqualTo(EventVisibility.PRIVATE);
                    assertThat(response.entryType()).isEqualTo(EventEntryType.TICKET_REQUIRED);
                    assertThat(response.status()).isEqualTo(EventStatus.DRAFT);
                    assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                });

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    assertThat(event.getTitle()).isEqualTo("Updated Event");
                    assertThat(event.getLocation()).isEqualTo("Updated location");
                    assertThat(event.getCapacity()).isEqualTo(100);
                    assertThat(event.getEntryType()).isEqualTo(EventEntryType.TICKET_REQUIRED);
                    assertThat(event.getOrganizer().getId()).isEqualTo(organizer.getId());
                });
    }

    @Test
    void shouldReturnOkForDraftWhenTitleIsNull() {
        event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
        EventUpdateRequest request = new EventUpdateRequest(
                null, JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined(),
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );

        update(request, accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.title()).isEqualTo("Test Event");
                });

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> assertThat(event.getTitle()).isEqualTo("Test Event"));
    }

    @Nested
    class WhenUpdatingEventTimes {

        @Test
        void shouldReturnBadRequestForDraftWhenTimeRangeIsInvalid() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            EventUpdateRequest request = new EventUpdateRequest(
                    null,
                    JsonNullable.undefined(), JsonNullable.undefined(),
                    JsonNullable.of(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(2))),
                    JsonNullable.of(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(1))),
                    JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
            );

            update(request, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.validationErrors()).containsEntry("updateTimeRange", "Invalid time range");
                    });
        }

        @Test
        void shouldReturnBadRequestForDraftWhenStartTimeIsInPast() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            EventUpdateRequest request = new EventUpdateRequest(
                    null,
                    JsonNullable.undefined(), JsonNullable.undefined(),
                    JsonNullable.of(TimeTestConfig.TEST_NOW.minus(Duration.ofHours(1))),
                    JsonNullable.of(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(1))),
                    JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
            );

            update(request, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.details()).contains("Start time must be in the future");
                    });
        }
    }

    @Test
    void shouldReturnBadRequestForPublishedWhenRequestIsInvalid() {
        event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

        EventUpdateRequest request = new EventUpdateRequest(
                null,
                JsonNullable.of(null),
                JsonNullable.undefined(),
                JsonNullable.of(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(2))),
                JsonNullable.of(TimeTestConfig.TEST_NOW.plus(Duration.ofHours(3))),
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );

        update(request, accessToken)
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                    assertThat(response.details())
                            .contains("Location cannot be cleared for a published event")
                            .contains("Cannot change start time on a published event")
                            .contains("Cannot change end time on a published event");
                });
    }

    @Nested
    class WhenEventIsInvalidState {

        @Test
        void shouldReturnConflictWhenEventIsDeleted() {
            event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            update(request, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Cannot update an event that is deleted");
                    });
        }

        @Test
        void shouldReturnConflictWhenEventIsFinished() {
            event = eventJpaRepository.save(EventTestFactory.createFinished(1L, organizer));
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            update(request, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Cannot update an event that is finished");
                    });
        }
    }

    @Nested
    class WhenEventIsCancelled {

        @BeforeEach
        void setUpCancelledEvent() {
            event = eventJpaRepository.save(EventTestFactory.createCancelled(1L, organizer));
        }

        @Test
        void shouldReturnOkWhenOnlyDescriptionIsProvided() {
            EventUpdateRequest request = new EventUpdateRequest(
                    null,
                    JsonNullable.undefined(),
                    JsonNullable.of("Updated description"),
                    JsonNullable.undefined(), JsonNullable.undefined(),
                    JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
            );

            update(request, accessToken)
                    .expectStatus().isOk()
                    .expectBody(EventResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.description()).isEqualTo("Updated description");
                    });

            assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                    .hasValueSatisfying(event -> {
                        assertThat(event.getDescription()).isEqualTo("Updated description");
                    });
        }

        @Test
        void shouldReturnConflictWhenOnlyTitleIsProvided() {
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            update(request, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Only the description can be changed");
                    });
        }

        @Test
        void shouldReturnConflictWhenTitleAndDescriptionAreProvided() {
            EventUpdateRequest request = new EventUpdateRequest(
                    "Updated Event",
                    JsonNullable.undefined(),
                    JsonNullable.of("Update description"),
                    JsonNullable.undefined(), JsonNullable.undefined(),
                    JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
            );

            update(request, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Only the description can be changed");
                    });
        }
    }

    @Nested
    class WhenUserIsNotOrganizer {
        private String otherAccessToken;

        @BeforeEach
        void setUpOtherUser() {
            otherAccessToken = createOtherUserAccessToken();
        }

        @Test
        void shouldReturnNotFoundForDraftPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            expectEventNotFound(update(request, otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            expectEventNotFound(update(request, otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            expectEventNotFound(update(request, otherAccessToken));
        }

        @Test
        void shouldReturnForbiddenForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));
            EventUpdateRequest request = createEventUpdateRequestTitleOnly();

            expectEventForbidden(update(request, otherAccessToken));
        }
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        EventUpdateRequest request = createEventUpdateRequestTitleOnly();

        var response = restTestClient.patch()
                .uri(eventPath(999999L))
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(accessToken);
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(request)
                .exchange();

        expectEventNotFound(response);
    }

    private static EventUpdateRequest createEventUpdateRequestTitleOnly() {
        return new EventUpdateRequest(
                "Updated Event", JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined(),
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );
    }

    private RestTestClient.ResponseSpec update(EventUpdateRequest request, String accessToken) {
        return restTestClient.patch()
                .uri(eventPath(event.getPublicId()))
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(accessToken);
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(request)
                .exchange();
    }

    private static String eventPath(Long publicId) {
        return EVENT_PATH + "/" + publicId;
    }
}
