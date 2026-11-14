package com.tgle.planner.event.presentation.validator;

import com.tgle.planner.event.presentation.dto.request.EventCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.Instant;

public class EventTimeRangeValidator implements ConstraintValidator<EventTimeRange, EventCreateRequest> {

    private String startField;
    private String endField;

    @Override
    public void initialize(EventTimeRange constraintAnnotation) {
        startField = constraintAnnotation.startTime();
        endField = constraintAnnotation.endTime();
    }

    @Override
    public boolean isValid(EventCreateRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(value);
        Instant startTime = (Instant) wrapper.getPropertyValue(startField);
        Instant endTime = (Instant) wrapper.getPropertyValue(endField);
        if (startTime == null || endTime == null) {
            return true;
        }
        boolean isValid = endTime.isAfter(startTime);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("timeRange")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
