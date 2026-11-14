package com.tgle.planner.image.factory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class ImageTestFactory {

    private ImageTestFactory() {}

    public static byte[] createInvalidJpeg() {
        return "Invalid Image".getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] createJpeg(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] createDefaultJpeg() {
        return createJpeg(100, 100);
    }
}
