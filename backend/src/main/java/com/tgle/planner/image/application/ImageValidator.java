package com.tgle.planner.image.application;

import com.tgle.planner.core.errorhandling.exception.InvalidImageException;
import com.tgle.planner.core.properties.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ImageValidator {

    private final ImageStorageProperties properties;

    public void validateEventImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidImageException("Image is required");
        }
        if (image.getSize() > properties.eventImage().maxFileSize().toBytes()) {
            throw new InvalidImageException("Image exceeds maximum allowed size of 5MB");
        }

        String extension = getExtension(image.getOriginalFilename());

        if (!properties.eventImage().allowedExtensions().contains(extension)) {
            throw new InvalidImageException("Extension is not supported");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index + 1).toLowerCase() : "";
    }
}
