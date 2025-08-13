package com.ltineraryplanning.tripservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DestinationDatesWithinTripValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DestinationDatesWithinTrip {
    String message() default "Destination dates must be within trip start and end dates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
