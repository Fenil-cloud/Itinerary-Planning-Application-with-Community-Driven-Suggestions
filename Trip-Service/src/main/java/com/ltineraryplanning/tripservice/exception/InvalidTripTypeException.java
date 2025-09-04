package com.ltineraryplanning.tripservice.exception;

public class InvalidTripTypeException extends RuntimeException {
    public InvalidTripTypeException(String message) {
        super(message);
    }
}
