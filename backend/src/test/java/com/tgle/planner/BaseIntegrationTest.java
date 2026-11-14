package com.tgle.planner;

import com.tgle.planner.config.TimeTestConfig;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.nio.file.Path;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@Import({TimeTestConfig.class, TestcontainersConfig.class})
public abstract class BaseIntegrationTest {

    protected final RestTestClient restTestClient;

    protected BaseIntegrationTest(RestTestClient restTestClient) {
        this.restTestClient = restTestClient;
    }

    @TempDir
    protected static Path tempDir;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("application.backend.storage.base-directory", tempDir::toString);
    }
}
