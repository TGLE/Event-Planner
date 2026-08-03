package com.tgle.planner.mailpit;

import com.tgle.planner.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

public class MailpitIntegrationTest extends BaseIntegrationTest {

    @Container
    static GenericContainer<?> mailpit = new GenericContainer<>("axllent/mailpit:latest")
            .withExposedPorts(1025, 8025)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void configureMail(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailpit::getHost);
        registry.add("spring.mail.port", () -> mailpit.getMappedPort(1025));
    }

    protected RestClient getMailpitClient() {
        return RestClient.builder()
                .baseUrl("http://" + mailpit.getHost() + ":" + mailpit.getMappedPort(8025))
                .build();
    }

    @AfterEach
    void clearMailpit() {
        getMailpitClient().delete()
                .uri("/api/v1/messages")
                .retrieve()
                .toBodilessEntity();
    }
}
