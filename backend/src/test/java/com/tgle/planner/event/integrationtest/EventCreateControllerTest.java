package com.tgle.planner.event.integrationtest;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.event.presentation.dto.request.EventCreateRequest;
import com.tgle.planner.event.presentation.dto.response.EventResponse;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCreateControllerTest extends BaseEventControllerTest {

    EventCreateControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Test
    void shouldReturnOkWhenRequestIsValid() {
        EventCreateRequest request = new EventCreateRequest(
                "Test Event",
                "Test location",
                "This is a test event",
                TimeTestConfig.TEST_NOW.plus(Duration.ofHours(1)),
                TimeTestConfig.TEST_NOW.plus(Duration.ofHours(2)),
                null,
                EventVisibility.PRIVATE,
                EventEntryType.FREE
        );

        EventResponse response = create(request, accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.publicId()).isNotNull();
        assertThat(response.title()).isEqualTo(request.title());
        assertThat(response.location()).isEqualTo(request.location());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.startTime()).isEqualTo(request.startTime());
        assertThat(response.endTime()).isEqualTo(request.endTime());
        assertThat(response.capacity()).isEqualTo(request.capacity());
        assertThat(response.visibility()).isEqualTo(request.visibility());
        assertThat(response.entryType()).isEqualTo(request.entryType());
        assertThat(response.dates().publishedAt()).isNull();

        assertThat(eventJpaRepository.findByPublicId(response.publicId()))
                .hasValueSatisfying(event -> {
                    assertThat(event.getId()).isNotNull();
                    assertThat(event.getPublicId()).isEqualTo(response.publicId());
                    assertThat(event.getTitle()).isEqualTo(response.title());
                    assertThat(event.getLocation()).isEqualTo(response.location());
                    assertThat(event.getDescription()).isEqualTo(response.description());
                    assertThat(event.getStartTime()).isEqualTo(response.startTime());
                    assertThat(event.getEndTime()).isEqualTo(response.endTime());
                    assertThat(event.getVisibility()).isEqualTo(response.visibility());
                    assertThat(event.getEntryType()).isEqualTo(response.entryType());
                    assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
                    assertThat(event.getState()).isEqualTo(EventState.ACTIVE);
                    assertThat(event.getDates()).isNull();
                    assertThat(event.getOrganizer().getId()).isEqualTo(organizer.getId());
                });
    }

    @Nested
    class WhenCreatingEventTitles {

        @Test
        void shouldReturnOkWhenOnlyTitleIsProvided() {
            EventCreateRequest request = createEventCreateRequestTitleOnly("Test Event");

            EventResponse response = create(request, accessToken)
                    .expectStatus().isOk()
                    .expectBody(EventResponse.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(response).isNotNull();
            assertThat(response.publicId()).isNotNull();
            assertThat(response.title()).isEqualTo("Test Event");
            assertThat(response.location()).isNull();
            assertThat(response.description()).isNull();
            assertThat(response.startTime()).isNull();
            assertThat(response.endTime()).isNull();

            assertThat(eventJpaRepository.findByPublicId(response.publicId()))
                    .hasValueSatisfying(event -> {
                        assertThat(event.getId()).isNotNull();
                        assertThat(event.getPublicId()).isEqualTo(response.publicId());
                        assertThat(event.getTitle()).isEqualTo(response.title());
                        assertThat(event.getLocation()).isNull();
                        assertThat(event.getDescription()).isNull();
                        assertThat(event.getStartTime()).isNull();
                        assertThat(event.getEndTime()).isNull();
                        assertThat(event.getVisibility()).isEqualTo(EventVisibility.PRIVATE);
                        assertThat(event.getEntryType()).isEqualTo(EventEntryType.FREE);
                        assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
                        assertThat(event.getState()).isEqualTo(EventState.ACTIVE);
                        assertThat(event.getDates()).isNull();
                        assertThat(event.getOrganizer().getId()).isEqualTo(organizer.getId());
                    });
        }

        @Test
        void shouldReturnBadRequestWhenTitleIsNull() {
            EventCreateRequest request = createEventCreateRequestTitleOnly(null);

            create(request, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.validationErrors()).containsEntry("title", "Title is required");
                    });
        }

        @Test
        void shouldReturnBadRequestWhenTitleIsBlank() {
            EventCreateRequest request = createEventCreateRequestTitleOnly("");

            create(request, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.validationErrors()).extractingByKey("title").asString()
                                .contains("Title must be between 2 and 100 characters", "Title is required");
                    });
        }
    }

    @Nested
    class WhenCreatingEventTimes {

        @Test
        void shouldReturnBadRequestWhenTimeRangeIsInvalid() {
            EventCreateRequest request = new EventCreateRequest(
                    "Test Event",
                    null,
                    null,
                    TimeTestConfig.TEST_NOW.plus(Duration.ofHours(2)),
                    TimeTestConfig.TEST_NOW.plus(Duration.ofHours(1)),
                    null,
                    null,
                    null
            );

            create(request, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.validationErrors()).containsEntry("timeRange", "Invalid time range");
                    });
        }

        @Test
        void shouldReturnBadRequestWhenStartTimeIsInPast() {
            EventCreateRequest request = new EventCreateRequest(
                    "Test Event",
                    null,
                    null,
                    TimeTestConfig.TEST_NOW.minus(Duration.ofHours(1)),
                    TimeTestConfig.TEST_NOW.plus(Duration.ofHours(1)),
                    null,
                    null,
                    null
            );

            create(request, accessToken)
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
    void shouldReturnUnauthorizedWhenUserIsUnauthenticated() {
        EventCreateRequest request = createEventCreateRequestTitleOnly("Test Event");

        restTestClient.post()
                .uri(EVENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.AUTHENTICATION_FAILED.getCode());
                    assertThat(response.details()).contains("Full authentication is required to access this resource");
                });
    }

    private RestTestClient.ResponseSpec create(EventCreateRequest request, String accessToken) {
        return restTestClient.post()
                .uri(EVENT_PATH)
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(accessToken);
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(request)
                .exchange();
    }

    private static EventCreateRequest createEventCreateRequestTitleOnly(String title) {
        return new EventCreateRequest(
                title, null, null, null,
                null, null, null, null
        );
    }
}
