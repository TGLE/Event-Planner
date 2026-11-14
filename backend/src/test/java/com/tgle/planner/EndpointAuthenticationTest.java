package com.tgle.planner;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.stream.Stream;

public class EndpointAuthenticationTest extends BaseIntegrationTest {

    protected EndpointAuthenticationTest(RestTestClient restTestClient) {
        super(restTestClient);
    }

    @ParameterizedTest(name = "{0} {1} requires authentication")
    @MethodSource("protectedEndpoints")
    void shouldReturnUnauthorizedWithoutAccessToken(HttpMethod method, String path) {
        restTestClient.method(method)
                .uri(path)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    static Stream<Arguments> protectedEndpoints() {
        return Stream.of(
                Arguments.of(HttpMethod.POST, "/api/v1/events"),
                Arguments.of(HttpMethod.PATCH, "/api/v1/events/1"),
                Arguments.of(HttpMethod.POST, "/api/v1/events/publish"),
                Arguments.of(HttpMethod.POST, "/api/v1/events/cancel"),
                Arguments.of(HttpMethod.POST, "/api/v1/events/restore"),
                Arguments.of(HttpMethod.DELETE, "/api/v1/events/1"),
                Arguments.of(HttpMethod.GET, "/api/v1/events/me"),
                Arguments.of(HttpMethod.PATCH, "/api/v1/events/1/image")
        );
    }
}
