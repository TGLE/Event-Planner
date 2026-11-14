package com.tgle.planner.event.integrationtest;

import com.tgle.planner.config.TimeTestConfig;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventControllerTest;
import com.tgle.planner.event.domain.enums.EventState;
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

public class EventDeleteControllerTest extends BaseEventControllerTest {

    private EventEntity event;

    EventDeleteControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Test
    void shouldReturnOkWhenEventIsActive() {
        event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

        delete(accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.state()).isEqualTo(EventState.DELETED);
                    assertThat(response.dates().deletedAt()).isEqualTo(TimeTestConfig.TEST_NOW);
                });

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    assertThat(event.getState()).isEqualTo(EventState.DELETED);
                    assertThat(event.getDates().getDeletedAt()).isEqualTo(TimeTestConfig.TEST_NOW);
                });
    }

    @Test
    void shouldReturnConflictWhenEventIsAlreadyDeleted() {
        event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));

        delete(accessToken)
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                    assertThat(response.details()).contains("The event is already deleted");
                });
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

            expectEventNotFound(delete(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));

            expectEventNotFound(delete(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

            expectEventNotFound(delete(otherAccessToken));
        }

        @Test
        void shouldReturnForbiddenForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

            expectEventForbidden(delete(otherAccessToken));
        }
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        var response = restTestClient.delete()
                .uri(eventPath(999999L))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();

        expectEventNotFound(response);
    }

    private RestTestClient.ResponseSpec delete(String accessToken) {
        return restTestClient.delete()
                .uri(eventPath(event.getPublicId()))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();
    }

    private static String eventPath(Long publicId) {
        return EVENT_PATH + "/" + publicId;
    }
}
