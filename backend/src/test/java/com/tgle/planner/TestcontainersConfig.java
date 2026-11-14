package com.tgle.planner;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    MySQLContainer mySQLContainer() {
        return new MySQLContainer("mysql:8.0")
                .withDatabaseName("event_planner")
                .withUsername("root")
                .withPassword("password");
    }
}
