package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.exception.handler;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.exception.SuggestionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(SuggestionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseDTO handlerSuggestionNotFoundException(SuggestionNotFoundException exception){
        return new ResponseDTO("400",exception.getMessage(),null);
    }
}
