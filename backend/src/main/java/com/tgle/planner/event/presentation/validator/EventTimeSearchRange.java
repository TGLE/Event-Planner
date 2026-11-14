package com.tgle.planner.event.presentation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventTimeSearchRangeValidator.class)
public @interface EventTimeSearchRange {
    String message() default "Invalid event start time range";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String startsAfter() default "startsAfter";
    String startsBefore() default "startsBefore";
}
