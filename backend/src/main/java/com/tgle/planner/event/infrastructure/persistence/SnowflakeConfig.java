package com.tgle.planner.event.infrastructure.persistence;

import com.tgle.planner.event.domain.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SnowflakeConfig {

    private static final long NODE_ID = 0L;

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(Clock clock) {
        return new SnowflakeIdGenerator(NODE_ID, clock);
    }
}
