package com.tgle.planner.mailpit;

import org.junit.jupiter.api.AfterEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;

public interface MailpitInterface {

    GenericContainer<?> mailpit = startMailpit();

    private static GenericContainer<?> startMailpit() {
        GenericContainer<?> container = new GenericContainer<>("axllent/mailpit:latest")
                .withExposedPorts(1025, 8025);
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void configureMail(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailpit::getHost);
        registry.add("spring.mail.port", () -> mailpit.getMappedPort(1025));
    }

    default RestClient getMailpitClient() {
        return RestClient.builder()
                .baseUrl("http://" + mailpit.getHost() + ":" + mailpit.getMappedPort(8025))
                .build();
    }

    @AfterEach
    default void clearMailpit() {
        getMailpitClient().delete()
                .uri("/api/v1/messages")
                .retrieve()
                .toBodilessEntity();
    }
}
