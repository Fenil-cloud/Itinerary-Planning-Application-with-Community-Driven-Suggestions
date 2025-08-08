package com.ltineraryplanning.authservice.Exception.handler;

import com.ltineraryplanning.authservice.Exception.UserNotFoundException;
import com.ltineraryplanning.authservice.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO handlerUserNotFoundException(UserNotFoundException exception){
        return new ResponseDTO("400",exception.getMessage(),null);
    }
}
