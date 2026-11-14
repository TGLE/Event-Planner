package com.tgle.planner.event.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class EventCapacityRangeValidator implements ConstraintValidator<EventCapacityRange, Object> {

    private String minField;
    private String maxField;

    @Override
    public void initialize(EventCapacityRange constraintAnnotation) {
        minField = constraintAnnotation.minCapacity();
        maxField = constraintAnnotation.maxCapacity();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(value);
        Integer minCapacity = (Integer) wrapper.getPropertyValue(minField);
        Integer maxCapacity = (Integer) wrapper.getPropertyValue(maxField);
        if (minCapacity == null || maxCapacity == null) {
            return true;
        }
        boolean isValid = minCapacity <= maxCapacity;
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("capacityRange")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
