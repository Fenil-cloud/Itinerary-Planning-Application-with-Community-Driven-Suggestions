package com.ltineraryplanning.tripservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ltineraryplanning.tripservice.exception.InvalidTripTypeException;
import lombok.Getter;

@Getter
public enum TripType {
    SOLO_TRIP("SOLO_TRIP"),
    BUSINESS_TRIP("BUSINESS_TRIP"),
    FAMILY_TRIP("FAMILY_TRIP");

    TripType(String tripType){
        this.tripType = tripType;
    }
    @JsonCreator
    public static TripType fromValue(String value) {
        for (TripType type : values()) {
            if (type.tripType.equalsIgnoreCase(value)) {
                return type;
            }
        }
        // Let it throw IllegalArgumentException
        throw new InvalidTripTypeException(
                "Invalid TripType: " + value + ". Allowed values: SOLO_TRIP, BUSINESS_TRIP, FAMILY_TRIP"
        );
    }
    private final String tripType;
}
