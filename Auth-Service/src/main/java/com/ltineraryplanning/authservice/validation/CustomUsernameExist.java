package com.ltineraryplanning.authservice.validation;

import com.ltineraryplanning.authservice.repo.UserRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomUsernameExist implements ConstraintValidator<UserNameValidation,String> {

    @Autowired
    private UserRepo repo;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if(repo.existsByUsername(username)){
            return false;
        }
        else {
            return true;
        }
    }
}
