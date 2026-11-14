package com.tgle.planner.event.baseintegration;

import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.repository.EventJpaRepository;
import com.tgle.planner.image.application.ImageStorage;
import com.tgle.planner.image.factory.ImageTestFactory;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import com.tgle.planner.util.JwtTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BaseEventImageControllerTest extends BaseEventControllerTest {

    protected final byte[] image = ImageTestFactory.createDefaultJpeg();
    protected final ImageStorage imageStorage;

    protected BaseEventImageControllerTest(
            RestTestClient restTestClient,
            UserJpaRepository userJpaRepository,
            EventJpaRepository eventJpaRepository,
            JwtTestUtil jwtTestUtil,
            ImageStorage imageStorage
    ) {
        super(restTestClient, userJpaRepository, eventJpaRepository, jwtTestUtil);
        this.imageStorage = imageStorage;
    }

    @AfterEach
    void cleanStorage() throws IOException {
        try (Stream<Path> files = Files.walk(tempDir)) {
            for (Path file: files.sorted(Comparator.reverseOrder()).toList()) {
                if (!file.equals(tempDir)) {
                    Files.delete(file);
                }
            }
        }
    }

    protected EventEntity saveEventWithImage(EventEntity.EventEntityBuilder eventBuilder) {
        return saveEventWithImage(eventBuilder, 1L);
    }

    protected EventEntity saveEventWithImage(EventEntity.EventEntityBuilder eventBuilder, Long publicId) {
        return eventJpaRepository.save(eventBuilder.imagePath(storeImage(publicId)).build());
    }

    protected String storeImage(Long publicId) {
        return imageStorage.store("events/" + publicId, new ByteArrayInputStream(image));
    }
}
