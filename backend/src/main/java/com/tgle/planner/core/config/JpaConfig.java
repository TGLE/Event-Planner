package com.tgle.planner.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dataTimeProvider")
public class JpaConfig {

    @Bean
    public DateTimeProvider dataTimeProvider(Clock clock) {
        return () -> Optional.of(clock.instant());
    }
}
