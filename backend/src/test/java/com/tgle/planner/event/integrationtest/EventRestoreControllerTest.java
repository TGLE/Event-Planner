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
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class EventRestoreControllerTest extends BaseEventControllerTest {

    private EventEntity event;

    EventRestoreControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
    }

    @Test
    void shouldReturnOkForDeletedDraftPrivateEvent() {
        event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));

        assertThat(event.getState()).isEqualTo(EventState.DELETED);

        restore(accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(event.getVisibility()).isEqualTo(EventVisibility.PRIVATE);
                    assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
                    assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                    assertThat(response.dates().deletedAt()).isNull();
                });

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    assertThat(event.getVisibility()).isEqualTo(EventVisibility.PRIVATE);
                    assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
                    assertThat(event.getState()).isEqualTo(EventState.ACTIVE);
                    assertThat(event.getDates()).isNull();
                });
    }

    @Test
    void shouldReturnOkForDeletedPublishedPublicEvent() {
        event = eventJpaRepository.save(EventTestFactory.createDeletedPublishedPublic(1L, organizer));

        assertThat(event.getState()).isEqualTo(EventState.DELETED);

        restore(accessToken)
                .expectStatus().isOk()
                .expectBody(EventResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(event.getVisibility()).isEqualTo(EventVisibility.PUBLIC);
                    assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
                    assertThat(response.state()).isEqualTo(EventState.ACTIVE);
                    assertThat(response.dates().deletedAt()).isNull();
                });

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    assertThat(event.getVisibility()).isEqualTo(EventVisibility.PUBLIC);
                    assertThat(event.getStatus()).isEqualTo(EventStatus.PUBLISHED);
                    assertThat(event.getState()).isEqualTo(EventState.ACTIVE);
                    assertThat(event.getDates().getDeletedAt()).isNull();
                    assertThat(event.getDates().getPublishedAt()).isNotNull();
                });
    }

    @Test
    void shouldReturnConflictWhenEventIsNotDeleted() {
        event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

        restore(accessToken)
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                    assertThat(response.details()).contains("Only deleted events can be restored");
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
        void shouldReturnNotFoundForDeletedDraftPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createDeleted(1L, organizer));

            expectEventNotFound(restore(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDeletedDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.defaultPublicBuilder(1L, organizer)
                    .state(EventState.DELETED)
                    .build());

            expectEventNotFound(restore(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDeletedPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.publishedBuilder(1L, organizer)
                    .state(EventState.DELETED)
                    .build());

            expectEventNotFound(restore(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDeletedPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                    .state(EventState.DELETED)
                    .build());

            expectEventNotFound(restore(otherAccessToken));
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

    private RestTestClient.ResponseSpec restore(String accessToken) {
        return restTestClient.post()
                .uri(eventPath(event.getPublicId()))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();
    }

    private static String eventPath(Long publicId) {
        return EVENT_PATH + "/" + publicId + "/restore";
    }
}
