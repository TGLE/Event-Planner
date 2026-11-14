package com.tgle.planner.core.errorhandling.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidEventException extends RuntimeException {

    private final List<String> errors;

    public InvalidEventException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public InvalidEventException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = List.copyOf(errors);
    }
}
