package com.ltineraryplanning.authservice.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomUsernameExist.class)
public @interface UserNameValidation {
    String message() default "username already exist!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
