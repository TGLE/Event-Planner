package com.tgle.planner.event.integrationtest.image;

import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventImageControllerTest;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.image.application.ImageStorage;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class EventImageGetControllerTest extends BaseEventImageControllerTest {

    private EventEntity event;

    protected EventImageGetControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil,
            ImageStorage imageStorage
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil, imageStorage);
    }

    @Nested
    class WhenUserIsOrganizer {

        @Test
        void shouldReturnOkForDraftPrivate() {
            event = saveEventWithImage(EventTestFactory.defaultBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }

        @Test
        void shouldReturnOkForDraftPublic() {
            event = saveEventWithImage(EventTestFactory.defaultPublicBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }

        @Test
        void shouldReturnOkForPublishedPrivate() {
            event = saveEventWithImage(EventTestFactory.publishedBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }

        @Test
        void shouldReturnOkForPublishedPublic() {
            event = saveEventWithImage(EventTestFactory.publishedPublicBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }

        @Test
        void shouldReturnOkForCancelledPrivate() {
            event = saveEventWithImage(EventTestFactory.cancelledBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }

        @Test
        void shouldReturnOkForFinishedPrivate() {
            event = saveEventWithImage(EventTestFactory.finishedBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }

        @Test
        void shouldReturnOkForDeleted() {
            event = saveEventWithImage(EventTestFactory.deletedBuilder(1L, organizer));

            expectEventImage(getEventImage(accessToken));
        }
    }

    @Nested
    class WhenUserIsAnonymous {

        @Test
        void shouldReturnOkForPublishedPublic() {
            event = saveEventWithImage(EventTestFactory.publishedPublicBuilder(1L, organizer));

            expectEventImage(getEventImage());
        }

        @Test
        void shouldReturnOkForCancelledPublic() {
            event = saveEventWithImage(EventTestFactory.cancelledPublicBuilder(1L, organizer));

            expectEventImage(getEventImage());
        }

        @Test
        void shouldReturnOkForFinishedPublic() {
            event = saveEventWithImage(EventTestFactory.finishedPublicBuilder(1L, organizer));

            expectEventImage(getEventImage());
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = saveEventWithImage(EventTestFactory.defaultPublicBuilder(1L, organizer));

            expectEventNotFound(getEventImage());
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = saveEventWithImage(EventTestFactory.publishedBuilder(1L, organizer));

            expectEventNotFound(getEventImage());
        }

        @Test
        void shouldReturnNotFoundForCancelledPrivate() {
            event = saveEventWithImage(EventTestFactory.cancelledBuilder(1L, organizer));

            expectEventNotFound(getEventImage());
        }

        @Test
        void shouldReturnNotFoundForFinishedPrivate() {
            event = saveEventWithImage(EventTestFactory.finishedBuilder(1L, organizer));

            expectEventNotFound(getEventImage());
        }

        @Test
        void shouldReturnNotFoundForDeleted() {
            event = saveEventWithImage(EventTestFactory.deletedBuilder(1L, organizer));

            expectEventNotFound(getEventImage());
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
            event = saveEventWithImage(EventTestFactory.publishedPublicBuilder(1L, organizer));

            expectEventImage(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnOkForCancelledPublic() {
            event = saveEventWithImage(EventTestFactory.cancelledPublicBuilder(1L, organizer));

            expectEventImage(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnOkForFinishedPublic() {
            event = saveEventWithImage(EventTestFactory.finishedPublicBuilder(1L, organizer));

            expectEventImage(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPrivate() {
            event = saveEventWithImage(EventTestFactory.defaultBuilder(1L, organizer));

            expectEventNotFound(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = saveEventWithImage(EventTestFactory.defaultPublicBuilder(1L, organizer));

            expectEventNotFound(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = saveEventWithImage(EventTestFactory.publishedBuilder(1L, organizer));

            expectEventNotFound(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForCancelledPrivate() {
            event = saveEventWithImage(EventTestFactory.cancelledBuilder(1L, organizer));

            expectEventNotFound(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForFinishedPrivate() {
            event = saveEventWithImage(EventTestFactory.finishedBuilder(1L, organizer));

            expectEventNotFound(getEventImage(otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDeleted() {
            event = saveEventWithImage(EventTestFactory.deletedBuilder(1L, organizer));

            expectEventNotFound(getEventImage(otherAccessToken));
        }
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        var response = restTestClient.get()
                .uri(eventImagePath(999999L))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();

        expectEventNotFound(response);
    }

    @Test
    void shouldReturnNotFoundWhenEventHasNoImage() {
        event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

        getEventImage()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND.getCode());
                    assertThat(response.details()).contains("Image could not be found");
                });
    }

    @Test
    void shouldReturnNotFoundWhenImageFileDoesNotExist() {
        event = eventJpaRepository.save(EventTestFactory.publishedPublicBuilder(1L, organizer)
                .imagePath("events/1/image.jpg")
                .build());

        getEventImage()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.errorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND.getCode());
                    assertThat(response.details()).contains("Image could not be found");
                });
    }

    private void expectEventImage(RestTestClient.ResponseSpec responseSpec) {
        expectEventImage(responseSpec, image);
    }

    private void expectEventImage(RestTestClient.ResponseSpec responseSpec, byte[] image) {
        responseSpec
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.IMAGE_JPEG)
                .expectBody(byte[].class)
                .value(bytes -> assertThat(bytes).isEqualTo(image));
    }

    private RestTestClient.ResponseSpec getEventImage() {
        return restTestClient.get()
                .uri(eventImagePath(event.getPublicId()))
                .exchange();
    }

    private RestTestClient.ResponseSpec getEventImage(String accessToken) {
        return restTestClient.get()
                .uri(eventImagePath(event.getPublicId()))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .exchange();
    }

    private static String eventImagePath(Long publicId) {
        return EVENT_PATH + "/" + publicId + "/image";
    }
}
