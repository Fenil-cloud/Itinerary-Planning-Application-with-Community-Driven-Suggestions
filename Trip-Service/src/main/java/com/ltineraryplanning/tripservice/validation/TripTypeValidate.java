package com.ltineraryplanning.tripservice.validation;

import com.ltineraryplanning.tripservice.enums.TripType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class TripTypeValidate implements ConstraintValidator<TripTypeValidation,String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
//        return false;
        List<String> stringList = new ArrayList<>();
        stringList.add(TripType.FAMILY_TRIP.toString());
        stringList.add(TripType.BUSINESS_TRIP.toString());
        stringList.add(TripType.SOLO_TRIP.toString());

        if(stringList.contains(s)){
            return true;
        }
        else {
            return false;
        }
    }
}
