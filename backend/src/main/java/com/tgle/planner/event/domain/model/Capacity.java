package com.tgle.planner.event.domain.model;

import com.tgle.planner.core.errorhandling.exception.InvalidEventException;

public record Capacity(Integer value) {

    public Capacity {
        if (value != null && value <= 0) {
            throw new InvalidEventException("Capacity must be greater than 0");
        }
    }

    public static Capacity unlimited() {
        return new Capacity(null);
    }

    public static Capacity of(int value) {
        return new Capacity(value);
    }

    public boolean isUnlimited() {
        return value == null;
    }
}
