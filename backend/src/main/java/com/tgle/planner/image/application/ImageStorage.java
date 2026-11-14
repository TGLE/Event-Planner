package com.tgle.planner.image.application;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface ImageStorage {
    String store(String directory, InputStream inputStream);
    void delete(String path);
    Resource load(String path);
}
