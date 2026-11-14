package com.tgle.planner.event.application.dto.command;

import org.springframework.web.multipart.MultipartFile;

public record EventImageUploadCommand(
        MultipartFile image,
        Long publicId,
        Long userId
) {
}
