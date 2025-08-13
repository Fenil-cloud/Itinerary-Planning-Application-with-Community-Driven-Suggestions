package com.ltineraryplanning.tripservice.validation;

import com.ltineraryplanning.tripservice.dto.TripDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, TripDTO> {
    @Override
    public boolean isValid(TripDTO tripDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (tripDTO.getStartDate() == null || tripDTO.getEndDate() == null) {
            return false; // Other validations will catch nulls
        }
        return tripDTO.getEndDate().isAfter(tripDTO.getStartDate());
    }
}
