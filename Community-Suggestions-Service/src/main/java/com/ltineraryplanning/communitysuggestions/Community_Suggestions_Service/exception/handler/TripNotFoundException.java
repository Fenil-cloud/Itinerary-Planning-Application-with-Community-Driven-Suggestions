package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.exception.handler;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String message) {
        super(message);
    }
}
