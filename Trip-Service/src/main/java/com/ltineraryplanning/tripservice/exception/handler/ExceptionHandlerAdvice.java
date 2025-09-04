package com.ltineraryplanning.tripservice.exception.handler;

import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import com.ltineraryplanning.tripservice.exception.InvalidTripTypeException;
import com.ltineraryplanning.tripservice.exception.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(TripNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO handleInvalidUserRoleException(TripNotFoundException exception) {
        return new ResponseDTO(String.valueOf(HttpStatus.BAD_REQUEST.value()), exception.getMessage(), null);
    }

    @ExceptionHandler(InvalidTripTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO handleInvalidTripTypeException(InvalidTripTypeException exception) {
        return new ResponseDTO(String.valueOf(HttpStatus.BAD_REQUEST.value()), exception.getMessage(), null);
    }
}
