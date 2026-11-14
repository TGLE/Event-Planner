package com.tgle.planner.event.integrationtest.image;

import com.tgle.planner.core.errorhandling.ErrorCode;
import com.tgle.planner.core.errorhandling.ErrorResponse;
import com.tgle.planner.event.baseintegration.BaseEventImageControllerTest;
import com.tgle.planner.event.factory.EventTestFactory;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.image.application.ImageStorage;
import com.tgle.planner.image.factory.ImageTestFactory;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.util.unit.DataSize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EventImageUpdateControllerTest extends BaseEventImageControllerTest {

    private EventEntity event;

    EventImageUpdateControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil,
            ImageStorage imageStorage
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil, imageStorage);
    }

    @Test
    void shouldUpdateEventImage() {
        event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

        assertThat(event.getImagePath()).isNull();

        update(multipartImage(ImageTestFactory.createDefaultJpeg()), accessToken)
                .expectStatus().isNoContent();

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    String imagePath = event.getImagePath();

                    assertThat(imagePath).isNotNull();
                    assertThat(tempDir.resolve(imagePath))
                            .exists()
                            .isRegularFile();
                });
    }

    @Test
    void shouldReplaceExistingEventImage() {
        event = saveEventWithImage(EventTestFactory.defaultBuilder(1L, organizer), 2L);
        String oldImagePath = event.getImagePath();

        assertThat(oldImagePath).isNotNull();

        Path oldImageFile = tempDir.resolve(oldImagePath);

        assertThat(oldImageFile)
                .exists()
                .isRegularFile();

        update(multipartImage(ImageTestFactory.createJpeg(200, 200)), accessToken)
                .expectStatus().isNoContent();

        assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                .hasValueSatisfying(event -> {
                    String newImagePath = event.getImagePath();

                    assertThat(newImagePath)
                            .isNotNull()
                            .isNotEqualTo(oldImagePath);
                    assertThat(tempDir.resolve(newImagePath))
                            .exists()
                            .isRegularFile();
                });

        assertThat(oldImageFile).doesNotExist();
    }

    @Test
    void shouldResizeImageExceedingMaxDimensions() throws IOException {
        event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

        var imageBody = multipartImage(ImageTestFactory.createJpeg(2560, 1440));

        update(imageBody, accessToken)
                .expectStatus().isNoContent();

        String imagePath = eventJpaRepository
                .findByPublicId(event.getPublicId())
                .map(EventEntity::getImagePath)
                .orElseThrow();

        assertThat(imagePath).isNotNull();

        Path imageFile = tempDir.resolve(imagePath);

        assertThat(imageFile)
                .exists()
                .isRegularFile();

        try (InputStream inputStream = Files.newInputStream(imageFile)) {
            BufferedImage processedImage = ImageIO.read(inputStream);

            assertThat(processedImage).isNotNull();
            assertThat(processedImage.getWidth()).isEqualTo(1920);
            assertThat(processedImage.getHeight()).isEqualTo(1080);
        }
    }

    @Nested
    class WhenImageIsInvalid {

        @BeforeEach
        void setUpEvent() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));
        }

        @Test
        void shouldReturnBadRequestWhenImageIsMissing() {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();

            update(builder.build(), accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.validationErrors()).containsEntry("image", "Image is required");
                    });
        }

        @Test
        void shouldReturnBadRequestWhenImageIsEmpty() {
            byte[] image = new byte[(int) DataSize.ofBytes(0).toBytes()];
            var imageBody = multipartImage(image);

            update(imageBody, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.details()).contains("Image is required");
                    });
        }

        @Test
        void shouldReturnBadRequestWhenImageExceedsMaxFileSize() {
            byte[] image = new byte[(int) DataSize.ofMegabytes(5).toBytes() + 1];
            var imageBody = multipartImage(image);

            update(imageBody, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.details()).contains("Image exceeds maximum allowed size of 5MB");
                    });
        }

        @Test
        void shouldReturnBadRequestWhenImageHasUnsupportedExtension() {
            byte[] image = ImageTestFactory.createDefaultJpeg();
            var imageBody = multipartBody(image, "image.gif", MediaType.IMAGE_GIF);

            update(imageBody, accessToken)
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED.getCode());
                        assertThat(response.details()).contains("Extension is not supported");
                    });
        }

        @Test
        void shouldReturnBadRequestWhenImageIsCorrupted() throws IOException {
            event = saveEventWithImage(EventTestFactory.defaultBuilder(2L, organizer), 2L);
            String oldImagePath = event.getImagePath();

            var imageBody = multipartImage(ImageTestFactory.createInvalidJpeg());

            update(imageBody, accessToken)
                    .expectStatus().isEqualTo(422)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.IMAGE_PROCESSING_FAILED.getCode());
                        assertThat(response.details()).contains("Could not process image");
                    });

            assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                    .hasValueSatisfying(event -> assertThat(event.getImagePath())
                            .isNotNull()
                            .isEqualTo(oldImagePath));

            assertThat(tempDir.resolve(oldImagePath)).exists();

            try (Stream<Path> files = Files.list(tempDir.resolve("events/2"))) {
                assertThat(files.count()).isEqualTo(1);
            }
        }
    }

    @Nested
    class WhenEventIsInvalidState {
        private MultiValueMap<String, HttpEntity<?>> imageBody;

        @BeforeEach
        void setUpImage() {
            imageBody = multipartImage(ImageTestFactory.createJpeg(200, 200));
        }

        @Test
        void shouldReturnConflictWhenEventIsCancelled() throws IOException {
            event = saveEventWithImage(EventTestFactory.cancelledBuilder(1L, organizer));
            String oldImagePath = event.getImagePath();
            Set<Path> filesBefore = listFiles(tempDir);

            update(imageBody, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Cannot update the image of an event that is cancelled");
                    });

            assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                    .hasValueSatisfying(event -> assertThat(event.getImagePath()).isEqualTo(oldImagePath));

            assertThat(tempDir.resolve(oldImagePath)).exists();
            assertThat(listFiles(tempDir)).isEqualTo(filesBefore);
        }

        @Test
        void shouldReturnConflictWhenEventIsFinished() throws IOException {
            event = saveEventWithImage(EventTestFactory.finishedBuilder(1L, organizer));
            String oldImagePath = event.getImagePath();
            Set<Path> filesBefore = listFiles(tempDir);

            update(imageBody, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Cannot update the image of an event that is finished");
                    });

            assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                    .hasValueSatisfying(event -> assertThat(event.getImagePath()).isEqualTo(oldImagePath));

            assertThat(tempDir.resolve(oldImagePath)).exists();
            assertThat(listFiles(tempDir)).isEqualTo(filesBefore);
        }

        @Test
        void shouldReturnConflictWhenEventIsDeleted() throws IOException {
            event = saveEventWithImage(EventTestFactory.deletedBuilder(1L, organizer));
            String oldImagePath = event.getImagePath();
            Set<Path> filesBefore = listFiles(tempDir);

            update(imageBody, accessToken)
                    .expectStatus().isEqualTo(409)
                    .expectBody(ErrorResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.errorCode()).isEqualTo(ErrorCode.DATA_CONFLICT.getCode());
                        assertThat(response.details()).contains("Cannot update the image of an event that is deleted");
                    });

            assertThat(eventJpaRepository.findByPublicId(event.getPublicId()))
                    .hasValueSatisfying(event -> assertThat(event.getImagePath()).isEqualTo(oldImagePath));

            assertThat(tempDir.resolve(oldImagePath)).exists();
            assertThat(listFiles(tempDir)).isEqualTo(filesBefore);
        }
    }

    @Nested
    class WhenUserIsNotOrganizer {
        private String otherAccessToken;
        private MultiValueMap<String, HttpEntity<?>> imageBody;

        @BeforeEach
        void setUpOtherUserAndImage() {
            otherAccessToken = createOtherUserAccessToken();
            imageBody = multipartImage(ImageTestFactory.createDefaultJpeg());
        }

        @Test
        void shouldReturnNotFoundForDraftPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createDefault(1L, organizer));

            expectEventNotFound(update(imageBody, otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForDraftPublic() {
            event = eventJpaRepository.save(EventTestFactory.createDraftPublic(1L, organizer));

            expectEventNotFound(update(imageBody, otherAccessToken));
        }

        @Test
        void shouldReturnNotFoundForPublishedPrivate() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPrivate(1L, organizer));

            expectEventNotFound(update(imageBody, otherAccessToken));
        }

        @Test
        void shouldReturnForbiddenForPublishedPublic() {
            event = eventJpaRepository.save(EventTestFactory.createPublishedPublic(1L, organizer));

            expectEventForbidden(update(imageBody, otherAccessToken));
        }
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        var response = restTestClient.patch()
                .uri(eventImagePath(999999L))
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(accessToken);
                    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .body(multipartImage(ImageTestFactory.createDefaultJpeg()))
                .exchange();

        expectEventNotFound(response);
    }

    private RestTestClient.ResponseSpec update(
            MultiValueMap<String, HttpEntity<?>> multipartImage, String accessToken
    ) {
        return restTestClient.patch()
                .uri(eventImagePath(event.getPublicId()))
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(accessToken);
                    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .body(multipartImage)
                .exchange();
    }

    private MultiValueMap<String, HttpEntity<?>> multipartImage(byte[] image) {
        return multipartBody(image, "image.jpg", MediaType.IMAGE_JPEG);
    }

    private MultiValueMap<String, HttpEntity<?>> multipartBody(
            byte[] content,
            String fileName,
            MediaType contentType
    ) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("image", new ByteArrayResource(content))
                .filename(fileName)
                .contentType(contentType);

        return builder.build();
    }

    private Set<Path> listFiles() throws IOException {
        return listFiles(tempDir);
    }

    private Set<Path> listFiles(Path dir) throws IOException {
        try (Stream<Path> files = Files.walk(dir)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(dir::relativize)
                    .collect(Collectors.toSet());
        }
    }

    private static String eventImagePath(Long publicId) {
        return EVENT_PATH + "/" + publicId + "/image";
    }
}
