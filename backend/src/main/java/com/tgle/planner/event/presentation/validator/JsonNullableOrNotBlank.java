package com.tgle.planner.event.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.openapitools.jackson.nullable.JsonNullable;

public class JsonNullableOrNotBlank implements ConstraintValidator<NullOrNotBlank, JsonNullable<String>> {

    @Override
    public boolean isValid(JsonNullable<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isUndefined()) {
            return true;
        }
        String inner = value.orElse(null);
        return inner == null || inner.isBlank();
    }
}
