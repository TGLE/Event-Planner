package com.tgle.planner.core.errorhandling.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource) {
        super(String.format("%s could not be found", resource));
    }

    public ResourceNotFoundException(String resource, String identifier, Object value) {
        super(String.format("%s could not be found with %s: '%s'", resource, identifier, value));
    }
}
