package com.tgle.planner.event.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.Instant;

public class EventTimeSearchRangeValidator implements ConstraintValidator<EventTimeSearchRange, Object> {

    private String afterField;
    private String beforeField;

    @Override
    public void initialize(EventTimeSearchRange constraintAnnotation) {
        afterField = constraintAnnotation.startsAfter();
        beforeField = constraintAnnotation.startsBefore();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(value);
        Instant startsAfter = (Instant) wrapper.getPropertyValue(afterField);
        Instant startsBefore = (Instant) wrapper.getPropertyValue(beforeField);
        if (startsAfter == null || startsBefore == null) {
            return true;
        }
        boolean isValid = !startsAfter.isAfter(startsBefore);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("timeSearchRange")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
