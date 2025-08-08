package com.ltineraryplanning.authservice.validation;

import com.ltineraryplanning.authservice.repo.UserRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomMobileNumExist implements ConstraintValidator<MobileNumValidation,String> {
    @Autowired
    private UserRepo repo;
    @Override
    public boolean isValid(String mobile, ConstraintValidatorContext constraintValidatorContext) {
     if(repo.existsByPhoneNumber(mobile)){
         return false;
     }
     else {
         return true;
     }
    }
}
