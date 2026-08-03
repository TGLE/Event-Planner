package com.tgle.planner.core.errorhandling.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resource) {
        super(String.format("%s already exists", resource));
    }

    public ResourceAlreadyExistsException(String resource, String identifier, Object value) {
        super(String.format("%s already exists with %s: '%s'", resource, identifier, value));
    }
}