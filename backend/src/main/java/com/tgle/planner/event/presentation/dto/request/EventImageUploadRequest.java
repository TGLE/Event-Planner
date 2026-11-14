package com.tgle.planner.event.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record EventImageUploadRequest(
        @NotNull(message = "Image is required")
        MultipartFile image
) {
}
