package com.tgle.planner.event.baseintegration;

import com.tgle.planner.BaseIntegrationTest;
import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.user.factory.UserTestFactory;
import com.tgle.planner.user.infrastructure.persistence.UserEntity;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseEventControllerTest extends BaseIntegrationTest {

    protected static final String EVENT_PATH = "/api/v1/events";

    protected final UserJpaRepository userJpaRepository;
    protected final EventJpaRepository eventJpaRepository;
    protected final JwtTestUtil jwtTestUtil;

    protected UserEntity organizer;
    protected String accessToken;

    protected BaseEventControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil
    ) {
        super(restTestClient);
        this.userJpaRepository = userJpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.jwtTestUtil = jwtTestUtil;
    }

    @BeforeEach
    void setUp() {
        eventJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();

        organizer = userJpaRepository.save(UserTestFactory.createDefault());
        accessToken = jwtTestUtil.createAccessToken(organizer);
    }

    protected String createOtherUserAccessToken() {
        UserEntity otherUser = userJpaRepository.save(UserTestFactory.createDefault("JaneDoe@gmail.com"));
        return jwtTestUtil.createAccessToken(otherUser);
    }

    protected void expectEventForbidden(RestTestClient.ResponseSpec responseSpec) {
        expectErrorResponse(
                responseSpec.expectStatus().isForbidden(),
                ErrorCode.ACCESS_DENIED,
                "Invalid organizer cannot access this resource"
        );
    }

    protected void expectEventNotFound(RestTestClient.ResponseSpec responseSpec) {
        expectErrorResponse(
                responseSpec.expectStatus().isNotFound(),
                ErrorCode.RESOURCE_NOT_FOUND,
                "Event could not be found"
        );
    }

    private void expectErrorResponse(
            RestTestClient.ResponseSpec responseSpec,
            ErrorCode expectedErrorCode,
            String expectedDetailMessage
    ) {
        responseSpec
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(expectedErrorCode.getCode());
                    assertThat(response.details()).contains(expectedDetailMessage);
                });
    }
}
