package com.tgle.planner.authentication;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import com.tgle.planner.BaseIntegrationTest;
import com.tgle.planner.authentication.presentation.dto.AuthenticationResponse;
import com.tgle.planner.authentication.presentation.dto.LoginRequest;
import com.tgle.planner.authentication.presentation.dto.RegisterRequest;
import com.tgle.planner.authentication.presentation.dto.VerifyOtpRequest;
import com.tgle.planner.mailpit.MailpitInterface;
import com.tgle.planner.mailpit.MailpitMessageResponse;
import com.tgle.planner.mailpit.MailpitSummaryResponse;
import com.tgle.planner.token.infrastructure.persistence.TokenJpaRepository;
import com.tgle.planner.user.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationControllerTest extends BaseIntegrationTest implements MailpitInterface {

    private static final String AUTH_PATH = "/api/v1/auth";
    private static final Pattern OTP_PATTERN = Pattern.compile("\\b\\d{6}\\b");

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    @BeforeEach
    public void cleanup() {
        userJpaRepository.deleteAllInBatch();
        tokenJpaRepository.deleteAllInBatch();
    }

    @Test
    void shouldRegisterAndVerifyAndLoginUser() {
        String userEmail = "JohnDoe@gmail.com";
        String userPassword = "password123";
        RegisterRequest registerRequest = new RegisterRequest(
                "John", "Doe", userEmail, userPassword, userPassword
        );
        restTestClient.post()
                .uri(AUTH_PATH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerRequest)
                .exchange()
                .expectStatus().isOk();

        assertThat(userJpaRepository.findByEmail(userEmail))
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isNotNull();
                    assertThat(user.getFirstName()).isEqualTo("John");
                    assertThat(user.getLastName()).isEqualTo("Doe");
                    assertThat(user.getEmail()).isEqualTo(userEmail);
                });

        AtomicReference<String> verificationCode = new AtomicReference<>();

        await().atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    MailpitSummaryResponse summary = getMailpitClient().get()
                            .uri("/api/v1/messages")
                            .retrieve()
                            .body(MailpitSummaryResponse.class);

                    assertThat(summary.messages()).isNotEmpty();

                    MailpitSummaryResponse.MailpitMessage verificationEmail = summary.messages().stream()
                            .filter(message -> message.Subject().toLowerCase().contains("verify")
                                    && message.Subject().toLowerCase().contains("email"))
                            .filter(message -> message.To().stream()
                                    .anyMatch(to -> to.Address().equalsIgnoreCase(userEmail)))
                            .findFirst().orElseThrow(AssertionError::new);

                    assertThat(verificationEmail).isNotNull();

                    MailpitMessageResponse message = getMailpitClient().get()
                            .uri("/api/v1/message/{id}", verificationEmail.ID())
                            .retrieve()
                            .body(MailpitMessageResponse.class);

                    assertThat(message).isNotNull();
                    Matcher matcher = OTP_PATTERN.matcher(message.Text());
                    assertThat(matcher.find()).isTrue();
                    verificationCode.set(matcher.group());
                });

        VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest(userEmail, verificationCode.get());

        restTestClient.post()
                .uri(AUTH_PATH + "/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .body(verifyOtpRequest)
                .exchange()
                .expectStatus().isOk();

        LoginRequest loginRequest = new LoginRequest(userEmail, userPassword, true);
        String deviceId = UUID.randomUUID().toString();

        restTestClient.post()
                .uri(AUTH_PATH + "/login")
                .header("X-Device-Id", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectCookie()
                .value("refresh-token", value -> assertThat(value).isNotBlank())
                .expectBody(AuthenticationResponse.class)
                .value(response -> assertThat(response.accessToken()).isNotBlank());
    }
}
