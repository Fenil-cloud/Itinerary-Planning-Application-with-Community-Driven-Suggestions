package com.ltineraryplanning.tripservice.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String message) {
        super(message);
    }
}
