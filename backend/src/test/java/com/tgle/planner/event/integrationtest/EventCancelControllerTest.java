package com.tgle.planner.event.integrationtest;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventStatus;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.event.presentation.dto.response.EventResponse;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCancelControllerTest extends BaseEventControllerTest {

    private EventEntity event;

    EventCancelControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Test
    void shouldReturnOkWhenEventIsActivePublished() {
        event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

        cancel(accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.status()).isEqualTo(EventStatus.CANCELLED);
                    assertThat(response.dates().cancelledAt()).isEqualTo(TimeTestConfig.TEST_NOW);
                });

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    assertThat(event.getStatus()).isEqualTo(EventStatus.CANCELLED);
                    assertThat(event.getDates().getCancelledAt()).isEqualTo(TimeTestConfig.TEST_NOW);
                });
    }

    @Nested
    class WhenEventIsInvalidState {

        @Test
        void shouldReturnConflictWhenEventIsAlreadyCancelled() {
            event = eventJpaRepository.save(EventTestFactory.createCancelled(1L, organizer));

            cancel(accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("The event is already cancelled");
                    });
        }

        @Test
        void shouldReturnConflictWhenEventIsDeleted() {
            event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));

            cancel(accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Cannot cancel an event that is deleted");
                    });
        }

        @Test
        void shouldReturnConflictWhenEventIsNotPublished() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            cancel(accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Only published events can be cancelled");
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

            expectEventNotFound(cancel(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));

            expectEventNotFound(cancel(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

            expectEventNotFound(cancel(otherAccessToken));
        }

        @Test
        void shouldReturnForbiddenForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

            expectEventForbidden(cancel(otherAccessToken));
        }
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        var response = restTestClient.post()
                .uri(eventPath(999999L))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();

        expectEventNotFound(response);
    }

    private static String eventPath(Long publicId) {
        return EVENT_PATH + "/" + publicId + "/cancel";
    }

    private RestTestClient.ResponseSpec cancel(String accessToken) {
        return restTestClient.post()
                .uri(eventPath(event.getPublicId()))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();
    }
}
