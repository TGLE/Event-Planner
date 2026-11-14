package com.tgle.planner.event.integrationtest;

import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventState;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.domain.enums.EventVisibility;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.event.presentation.dto.response.EventResponse;
import com.tgle.planner.event.presentation.dto.response.PublicEventResponse;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class EventGetControllerTest extends BaseEventControllerTest {

    private EventEntity event;

    EventGetControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Nested
    class WhenUserIsOrganizer {

        @Test
        void shouldReturnOkForDraftPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            getEvent(accessToken)
                    .expectStatus().isOk()
                    .expectBody(EventResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.publicId()).isEqualTo(event.getPublicId());
                        assertThat(response.visibility()).isEqualTo(EventVisibility.PRIVATE);
                        assertThat(response.status()).isEqualTo(EventStatus.DRAFT);
                        assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                        assertThat(response.dates()).isNotNull();
                        assertThat(response.dates().publishedAt()).isNull();
                    });
        }

        @Test
        void shouldReturnOkForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));

            getEvent(accessToken)
                    .expectStatus().isOk()
                    .expectBody(EventResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.publicId()).isEqualTo(event.getPublicId());
                        assertThat(response.visibility()).isEqualTo(EventVisibility.PUBLIC);
                        assertThat(response.status()).isEqualTo(EventStatus.DRAFT);
                        assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                        assertThat(response.dates()).isNotNull();
                        assertThat(response.dates().publishedAt()).isNull();
                    });
        }

        @Test
        void shouldReturnOkForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

            getEvent(accessToken)
                    .expectStatus().isOk()
                    .expectBody(EventResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.publicId()).isEqualTo(event.getPublicId());
                        assertThat(response.visibility()).isEqualTo(EventVisibility.PRIVATE);
                        assertThat(response.status()).isEqualTo(EventStatus.PUBLISHED);
                        assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                        assertThat(response.dates().publishedAt()).isNotNull();
                    });
        }

        @Test
        void shouldReturnOkForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

            getEvent(accessToken)
                    .expectStatus().isOk()
                    .expectBody(EventResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.publicId()).isEqualTo(event.getPublicId());
                        assertThat(response.visibility()).isEqualTo(EventVisibility.PUBLIC);
                        assertThat(response.status()).isEqualTo(EventStatus.PUBLISHED);
                        assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                        assertThat(response.dates().publishedAt()).isNotNull();
                    });
        }
    }

    @Nested
    class WhenUserIsAnonymous {

        @Test
        void shouldReturnOkForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

            getEvent()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.publicId").exists()
                    .jsonPath("$.visibility").doesNotExist()
                    .jsonPath("$.status").isEqualTo(EventStatus.PUBLISHED.toString())
                    .jsonPath("$.state").doesNotExist()
                    .jsonPath("$.dates.publishedAt").exists();
        }

        @Test
        void shouldReturnOkForCancelledPublic() {
            event = eventJpaRepository.save(EventTestFactory.createCancelledPublic(1L, organizer));

            getEvent()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.publicId").exists()
                    .jsonPath("$.visibility").doesNotExist()
                    .jsonPath("$.status").isEqualTo(EventStatus.CANCELLED.toString())
                    .jsonPath("$.state").doesNotExist()
                    .jsonPath("$.dates.publishedAt").exists()
                    .jsonPath("$.dates.cancelledAt").exists();
        }

        @Test
        void shouldReturnOkForFinishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createFinishedPublic(1L, organizer));

            getEvent()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.publicId").exists()
                    .jsonPath("$.visibility").doesNotExist()
                    .jsonPath("$.status").isEqualTo(EventStatus.FINISHED.toString())
                    .jsonPath("$.state").doesNotExist()
                    .jsonPath("$.dates.publishedAt").exists()
                    .jsonPath("$.dates.finishedAt").exists();
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));

            expectEventNotFound(getEvent());
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

            expectEventNotFound(getEvent());
        }

        @Test
        void shouldReturnNotFoundForCancelledPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createCancelled(1L, organizer));

            expectEventNotFound(getEvent());
        }

        @Test
        void shouldReturnNotFoundForFinishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createFinished(1L, organizer));

            expectEventNotFound(getEvent());
        }

        @Test
        void shouldReturnNotFoundForDeleted() {
            event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));

            expectEventNotFound(getEvent());
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
        void shouldReturnOkForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

            getEvent(otherAccessToken)
                    .expectStatus().isOk()
                    .expectBody(PublicEventResponse.class);
        }

        @Test
        void shouldReturnOkForCancelledPublic() {
            event = eventJpaRepository.save(EventTestFactory.createCancelledPublic(1L, organizer));

            getEvent(otherAccessToken)
                    .expectStatus().isOk()
                    .expectBody(PublicEventResponse.class);
        }

        @Test
        void shouldReturnOkForFinishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createFinishedPublic(1L, organizer));

            getEvent(otherAccessToken)
                    .expectStatus().isOk()
                    .expectBody(PublicEventResponse.class);
        }

        @Test
        void shouldReturnNotFoundForDraftPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            expectEventNotFound(getEvent(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));

            expectEventNotFound(getEvent(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

            expectEventNotFound(getEvent(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForCancelledPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createCancelled(1L, organizer));

            expectEventNotFound(getEvent(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForFinishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createFinished(1L, organizer));

            expectEventNotFound(getEvent(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDeleted() {
            event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));

            expectEventNotFound(getEvent(otherAccessToken));
        }
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        var response = restTestClient.get()
                .uri(eventPath(999999L))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();

        expectEventNotFound(response);
    }

    private RestTestClient.ResponseSpec getEvent() {
        return restTestClient.get()
                .uri(eventPath(event.getPublicId()))
                .exchange();
    }

    private RestTestClient.ResponseSpec getEvent(String accessToken) {
        return restTestClient.get()
                .uri(eventPath(event.getPublicId()))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();
    }

    private static String eventPath(Long publicId) {
        return EVENT_PATH + "/" + publicId;
    }
}
