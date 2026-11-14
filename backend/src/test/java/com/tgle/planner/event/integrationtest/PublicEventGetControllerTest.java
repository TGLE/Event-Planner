package com.tgle.planner.event.integrationtest;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventEntryType;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventDatesEmbeddable;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.event.presentation.dto.response.PublicEventSummaryResponse;
import com.tgle.planner.event.presentation.search.EventSortOption;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JsonPage;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicEventGetControllerTest extends BaseEventControllerTest {

    PublicEventGetControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Test
    void shouldReturnResultWhenFindingOnlyPublishedPublicEvent() {
        eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));
        eventJpaRepository.save(EventTestFactory.createPublishedPrivate(2L, organizer));
        eventJpaRepository.save(EventTestFactory.createDefault(3L, organizer));
        eventJpaRepository.save(EventTestFactory.createFinishedPublic(4L, organizer));
        eventJpaRepository.save(EventTestFactory.createCancelledPublic(5L, organizer));
        eventJpaRepository.save(EventTestFactory.publishedBuilder(6L, organizer)
                .state(EventState.DELETED)
                .build());

        restTestClient.get()
                .uri(EVENT_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getContent())
                            .hasSize(1)
                            .extracting(PublicEventSummaryResponse::publicId)
                            .containsExactly(1L);
                });
    }

    @Nested
    class WhenFilteringPublishedPublicEvents {

        @Test
        void shouldReturnResultWhenFilteringBySearchTerm() {
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .title("The FESTIVAL of LEGENDS").build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(2L, organizer)
                    .title("The Party").build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(3L, organizer)
                    .description("The festival closes soon!").build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("search", "festival")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(2);
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .containsExactlyInAnyOrder(1L, 3L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByLocation() {
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .location("Rooftop").build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(2L, organizer)
                    .location("pArTy house").build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(3L, organizer)
                    .location("Graveyard").build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("location", "party")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(1);
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .containsExactly(2L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByStartTime() {
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .startTime(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(1))).build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(2L, organizer)
                    .startTime(TimeTestConfig.TEST_NOW.plus(Duration.ofDays(1))).build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("startsAfter", TimeTestConfig.TEST_NOW)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(1);
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .containsExactly(2L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByCapacity() {
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .capacity(50).build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(2L, organizer)
                    .capacity(100).build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("minCapacity", 75)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(1);
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .containsExactly(2L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByEntryType() {
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .entryType(EventEntryType.FREE).build());
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(2L, organizer)
                    .entryType(EventEntryType.TICKET_REQUIRED).build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("entryType", EventEntryType.TICKET_REQUIRED)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getTotalElements()).isEqualTo(1);
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .containsExactly(2L);
                    });
        }

        @Test
        void shouldReturnResultWhenFilteringByPageNumberAndPageSize() {
            for (long publicId = 1; publicId <= 5; publicId++) {
                eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(publicId, organizer)
                        .dates(EventDatesEmbeddable.builder()
                                .publishedAt(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(publicId)))
                                .build())
                        .build());
            }

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("page", 1)
                            .queryParam("size", 2)
                            .build())
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
                                .containsExactlyInAnyOrder(3L, 4L);
                    });
        }

        @Test
        void shouldReturnResultWhenSortingByOldestFirst() {
            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .dates(EventDatesEmbeddable.builder()
                            .publishedAt(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(2)))
                            .build())
                    .build());

            eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(2L, organizer)
                    .dates(EventDatesEmbeddable.builder()
                            .publishedAt(TimeTestConfig.TEST_NOW.minus(Duration.ofDays(1)))
                            .build())
                    .build());

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("sortOption", EventSortOption.OLDEST)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<JsonPage<PublicEventSummaryResponse>>() {})
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getContent())
                                .extracting(PublicEventSummaryResponse::publicId)
                                .containsExactly(1L, 2L);
                    });
        }

        @Test
        void shouldReturnEmptyResultWhenNoEventsMatch() {
            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EVENT_PATH)
                            .queryParam("search", "Empty")
                            .build())
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
                        .path(EVENT_PATH)
                        .queryParam("startsAfter", TimeTestConfig.TEST_NOW.plus(Duration.ofDays(2)))
                        .queryParam("startsBefore", TimeTestConfig.TEST_NOW.plus(Duration.ofDays(1)))
                        .build())
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
                        .path(EVENT_PATH)
                        .queryParam("minCapacity", 100)
                        .queryParam("maxCapacity", 50)
                        .build())
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
