package com.tgle.planner.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.util.Set;

@ConfigurationProperties(prefix = "application.backend.storage")
public record ImageStorageProperties(
        String baseDirectory,
        EventImageProperties eventImage
) {
    public record EventImageProperties(
            Set<String> allowedExtensions,
            DataSize maxFileSize,
            int maxWidth,
            int maxHeight
    ) {
    }
}
