package com.tgle.planner.event.integrationtest;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.event.presentation.dto.response.EventSummaryResponse;
import com.tgle.planner.event.presentation.dto.response.PublicEventSummaryResponse;
import com.tgle.planner.event.presentation.search.EventSortOption;
import com.tgle.planner.user.factory.UserTestFactory;
import com.tgle.planner.user.infrastructure.persistence.UserEntity;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JsonPage;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizerEventGetControllerTest extends BaseEventControllerTest {

    private static final String MY_EVENT_PATH = EVENT_PATH + "/me";

    private final TimeTestConfig.TestClock testClock;

    OrganizerEventGetControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil, TimeTestConfig.TestClock testClock
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
        this.testClock = testClock;
    }

    @Test
    void shouldReturnResultWhenFindingEventsRegardlessOfStatus() {
        eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
        eventJpaRepository.save(EventTestFactory.createPublishedPublic(2L, organizer));
        eventJpaRepository.save(EventTestFactory.createPublishedPrivate(3L, organizer));
        eventJpaRepository.save(EventTestFactory.createCancelled(4L, organizer));
        eventJpaRepository.save(EventTestFactory.createFinished(5L, organizer));

        restTestClient.get()
                .uri(MY_EVENT_PATH)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalElements()).isEqualTo(5);
                    assertThat(response.getContent())
                            .extracting(EventSummaryResponse::publicId)
                            .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L);
                });
    }

    @Test
    void shouldReturnResultWhenFindingEventBelongingToOrganizer() {
        UserEntity otherUser = userJpaRepository.save(UserTestFactory.createDefault("JaneDoe@gmail.com"));
        eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
        eventJpaRepository.save(EventTestFactory.createDefault(2L, otherUser));
        eventJpaRepository.save(EventTestFactory.createPublishedPublic(3L, otherUser));

        restTestClient.get()
                .uri(MY_EVENT_PATH)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalElements()).isEqualTo(1);
                    assertThat(response.getContent())
                            .extracting(EventSummaryResponse::publicId)
                            .containsExactlyInAnyOrder(1L);
                });
    }

    @Test
    void shouldReturnResultWhenFindingEventExcludingDeleted() {
        eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
        eventJpaRepository.save(EventTestFactory.createDeleted(2L, organizer));

        restTestClient.get()
                .uri(MY_EVENT_PATH)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalElements()).isEqualTo(1);
                    assertThat(response.getContent())
                            .extracting(EventSummaryResponse::publicId)
                            .containsExactly(1L);
                });
    }

    @Nested
    class WhenFilteringOrganizerEvents {

        @Test
        void shouldReturnResultWhenFilteringBySearchTerm() {
            eventJpaRepository.save(EventTestFactory.defaultBuilder(1L, organizer)
                    .title("Spring Parade").build());
            eventJpaRepository.save(EventTestFactory.defaultBuilder(2L, organizer)
                    .description("Summer Parade").build());
            eventJpaRepository.save(EventTestFactory.defaultBuilder(3L, organizer)
                    .description("SPRING activities and fun!").build());
            eventJpaRepository.save(EventTestFactory.defaultBuilder(4L, organizer)
                    .location("sPrInG Street").build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("search", "spring")
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(3);
                        assertThat(response.getContent())
                                .extracting(EventSummaryResponse::publicId)
                                .containsExactlyInAnyOrder(1L, 3L, 4L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByVisibility() {
            eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));
            eventJpaRepository.save(EventTestFactory.createPublishedPublic(2L, organizer));

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("visibility", EventVisibility.PRIVATE)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(1);
                        assertThat(response.getContent())
                                .extracting(EventSummaryResponse::publicId)
                                .containsExactly(1L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByStatus() {
            eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
            eventJpaRepository.save(EventTestFactory.createPublishedPrivate(2L, organizer));
            eventJpaRepository.save(EventTestFactory.createPublishedPublic(3L, organizer));

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("status", EventStatus.PUBLISHED)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(2);
                        assertThat(response.getContent())
                                .extracting(EventSummaryResponse::publicId)
                                .containsExactlyInAnyOrder(2L, 3L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByState() {
            eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
            eventJpaRepository.save(EventTestFactory.createDeleted(2L, organizer));

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("state", EventState.DELETED)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(1);
                        assertThat(response.getContent())
                                .extracting(EventSummaryResponse::publicId)
                                .containsExactlyInAnyOrder(2L);
                    });
        }

        @Test
        void shouldReturnResultWhenSortingByNewestFirst() {
            eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            testClock.advance(Duration.ofDays(1));
            eventJpaRepository.save(EventTestFactory.createDefault(2L, organizer));

            testClock.advance(Duration.ofDays(1));
            eventJpaRepository.save(EventTestFactory.createDefault(3L, organizer));

            testClock.reset();

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("sortOption", EventSortOption.NEWEST)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<EventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getContent())
                                .extracting(EventSummaryResponse::publicId)
                                .containsExactly(3L, 2L, 1L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByPageNumberAndPageSize() {
            for (long publicId = 1; publicId <= 5; publicId++) {
                EventEntity event = eventJpaRepository.save(EventTestFactory.createDefault(publicId, organizer));
                System.out.println("ID: " + event.getPublicId() + " createdAt: " + event.getCreatedAt());
                testClock.advance(Duration.ofDays(1));
            }

            testClock.reset();

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("page", 1)
                            .queryParam("size", 2)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(5);
                        assertThat(response.getTotalPages()).isEqualTo(3);
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .hasSize(2)
                                .containsExactlyInAnyOrder(3L, 2L);
                    });
        }

        @Test
        void shouldReturnEmptyResultWhenNoEventsMatch() {
            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MY_EVENT_PATH)
                            .queryParam("search", "Empty")
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(0);
                    });
        }
    }

    @Test
    void shouldReturnBadRequestWhenStartsAfterComesAfterStartsBefore() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(MY_EVENT_PATH)
                        .queryParam("startsAfter", TimeTestConfig.TEST_NOW.plus(Duration.ofDays(2)))
                        .queryParam("startsBefore", TimeTestConfig.TEST_NOW.plus(Duration.ofDays(1)))
                        .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                    assertThat(response.validationErrors()).containsEntry
                            ("timeSearchRange", "Invalid event start time range");
                });
    }

    @Test
    void shouldReturnBadRequestWhenMinCapacityIsGreaterThanMaxCapacity() {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(MY_EVENT_PATH)
                        .queryParam("minCapacity", 100)
                        .queryParam("maxCapacity", 50)
                        .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                    assertThat(response.validationErrors()).containsEntry("capacityRange", "Invalid capacity range");
                });
    }
}
