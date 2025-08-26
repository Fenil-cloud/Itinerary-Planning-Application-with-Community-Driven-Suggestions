package com.ltineraryplanning.tripservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TripTypeValidate.class)
public @interface TripTypeValidation {
    String message() default "Trip not Valid!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
