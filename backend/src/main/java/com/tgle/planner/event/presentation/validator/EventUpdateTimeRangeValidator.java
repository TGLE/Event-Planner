package com.tgle.planner.event.presentation.validator;

import com.tgle.planner.event.presentation.dto.request.EventUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.Instant;

public class EventUpdateTimeRangeValidator implements ConstraintValidator<EventTimeRange, EventUpdateRequest> {

    private String startField;
    private String endField;

    @Override
    public void initialize(EventTimeRange constraintAnnotation) {
        startField = constraintAnnotation.startTime();
        endField = constraintAnnotation.endTime();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(EventUpdateRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(value);
        JsonNullable<Instant> startTime = (JsonNullable<Instant>) wrapper.getPropertyValue(startField);
        JsonNullable<Instant> endTime = (JsonNullable<Instant>) wrapper.getPropertyValue(endField);
        if (startTime == null || startTime.isUndefined() || endTime == null || endTime.isUndefined()) {
            return true;
        }
        Instant innerStart = startTime.orElse(null);
        Instant innerEnd = endTime.orElse(null);
        if (innerStart == null || innerEnd == null) {
            return true;
        }
        boolean isValid = innerEnd.isAfter(innerStart);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("updateTimeRange")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
