package com.tgle.planner.core.errorhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        Integer statusCode,
        String errorCode,
        String statusName,
        String errorMessage,
        Map<String, String> validationErrors,
        String details,
        Instant timestamp
) {
}
