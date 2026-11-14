package com.tgle.planner.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.*;

@TestConfiguration
@Profile("test")
public class TimeTestConfig {

    public static final Instant TEST_NOW = Instant.parse("2026-08-01T00:00:00Z");

    @Bean
    @Primary
    public TestClock clock() {
        return new TestClock(TEST_NOW);
    }

    public static class TestClock extends Clock {

        private Instant currentTime;

        public TestClock(Instant currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return currentTime;
        }

        public void advance(Duration duration) {
            currentTime = currentTime.plus(duration);
        }

        public void reset() {
            currentTime = TEST_NOW;
        }
    }
}
