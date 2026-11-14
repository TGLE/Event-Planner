package com.tgle.planner.event.presentation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EventTimeRangeValidator.class, EventUpdateTimeRangeValidator.class})
public @interface EventTimeRange {
    String message() default "Invalid time range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startTime() default "startTime";
    String endTime() default "endTime";
}
