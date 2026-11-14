package com.tgle.planner.image.application;

import com.tgle.planner.core.errorhandling.exception.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ImageProcessor {

    public InputStream process(InputStream inputStream, int maxWidth, int maxHeight) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(inputStream)
                    .size(maxWidth, maxHeight)
                    .outputFormat("jpg")
                    .outputQuality(0.9)
                    .toOutputStream(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new ImageProcessingException("Could not process image", e);
        }
    }
}
