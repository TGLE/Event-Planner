package com.tgle.planner.image.infrastructure;

import com.tgle.planner.core.errorhandling.exception.ImageStorageException;
import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.core.properties.ImageStorageProperties;
import com.tgle.planner.image.application.ImageStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageStorageImpl implements ImageStorage {

    private final ImageStorageProperties properties;

    @Override
    public String store(String directory, InputStream inputStream) {
        Path directoryPath = ensureSafePath(directory);
        String fileName = UUID.randomUUID() + ".jpg";
        Path target = directoryPath.resolve(fileName);

        try {
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target);

            return Path.of(directory)
                    .resolve(fileName)
                    .toString()
                    .replace('\\', '/');

        } catch (IOException e) {
            throw new ImageStorageException("Failed to store image", e);
        }
    }

    @Override
    public void delete(String path) {
        Path imagePath = ensureSafePath(path);

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new ImageStorageException("Failed to delete image", e);
        }
    }

    @Override
    public Resource load(String path) {
        Path imagePath = ensureSafePath(path);

        if (!Files.exists(imagePath) || !Files.isReadable(imagePath)) {
            throw new ResourceNotFoundException("Image", "path", path);
        }
        return new FileSystemResource(imagePath);
    }

    private Path ensureSafePath(String path) {
        Path basePath = Path.of(properties.baseDirectory())
                .toAbsolutePath()
                .normalize();

        Path resolved = basePath.resolve(path).normalize();

        if (!resolved.startsWith(basePath)) {
            throw new IllegalArgumentException("Invalid image path");
        }

        return resolved;
    }
}
