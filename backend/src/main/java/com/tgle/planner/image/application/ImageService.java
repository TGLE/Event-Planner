package com.tgle.planner.image.application;

import com.tgle.planner.core.errorhandling.exception.ImageProcessingException;
import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.core.properties.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageProcessor imageProcessor;
    private final ImageStorage imageStorage;
    private final ImageStorageProperties properties;

    public String storeEventImage(String directory, InputStream inputStream) {
        int maxWidth = properties.eventImage().maxWidth();
        int maxHeight = properties.eventImage().maxHeight();
        try (InputStream processedImage = imageProcessor.process(inputStream, maxWidth, maxHeight)) {
            return imageStorage.store(directory, processedImage);
        } catch (IOException e) {
            throw new ImageProcessingException("Could not process event image", e);
        }
    }

    public void deleteImage(String imagePath) {
        if (imagePath == null) {
            return;
        }
        try {
            imageStorage.delete(imagePath);
        } catch (Exception e) {
            log.error("Could not delete image: {}", imagePath, e);
        }
    }

    public Resource loadImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            throw new ResourceNotFoundException("Image", "path", imagePath);
        }
        return imageStorage.load(imagePath);
    }
}
