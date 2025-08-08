package com.ltineraryplanning.authservice.validation;

import com.ltineraryplanning.authservice.repo.UserRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomEmailExist implements ConstraintValidator<EmailValidation,String> {
    @Autowired
    private UserRepo repo;
    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(repo.existsByEmail(email)){
            return false;
        }
        else {
            return true;
        }
    }
}
