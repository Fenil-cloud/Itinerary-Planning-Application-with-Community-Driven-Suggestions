package com.ltineraryplanning.tripservice.validation;

import com.ltineraryplanning.tripservice.dto.DestinationDTO;
import com.ltineraryplanning.tripservice.dto.TripDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DestinationDatesWithinTripValidator implements ConstraintValidator<DestinationDatesWithinTrip, TripDTO> {
    @Override
    public boolean isValid(TripDTO tripDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (tripDTO == null || tripDTO.getDestinations() == null) {
            return true; // Let @NotNull handle null cases
        }

        boolean isValid = true;

        for (DestinationDTO dest : tripDTO.getDestinations()) {
            if (tripDTO.getStartDate() != null && dest.getStartDate() != null &&
                    dest.getStartDate().isBefore(tripDTO.getStartDate())) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "Destination start date " + dest.getStartDate() + " cannot be before trip start date " + tripDTO.getStartDate()
                ).addConstraintViolation();
                isValid = false;
            }

            if (tripDTO.getEndDate() != null && dest.getEndDate() != null &&
                    dest.getEndDate().isAfter(tripDTO.getEndDate())) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "Destination end date " + dest.getEndDate() + " cannot be after trip end date " + tripDTO.getEndDate()
                ).addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
