package com.tgle.planner.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        String name,
        BackendProperties backend
) {
    public record BackendProperties(
            MailProperties mail
    ) {
        public record MailProperties(
                String sender
        ) {
        }
    }
}
