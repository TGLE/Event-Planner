package com.tgle.planner.event.presentation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventCapacityRangeValidator.class)
public @interface EventCapacityRange {
    String message() default "Invalid capacity range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String minCapacity() default "minCapacity";
    String maxCapacity() default "maxCapacity";
}
