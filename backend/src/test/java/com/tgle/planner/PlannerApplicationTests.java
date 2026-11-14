package com.tgle.planner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
class PlannerApplicationTests extends BaseIntegrationTest {

	PlannerApplicationTests(RestTestClient restTestClient) {
		super(restTestClient);
	}

	@Test
	void contextLoads() {
	}

}
