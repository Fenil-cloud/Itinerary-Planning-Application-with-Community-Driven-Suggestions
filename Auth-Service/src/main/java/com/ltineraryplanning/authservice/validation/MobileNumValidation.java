package com.ltineraryplanning.authservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomMobileNumExist.class)
public @interface MobileNumValidation {
    String message() default "mobile already exist!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
