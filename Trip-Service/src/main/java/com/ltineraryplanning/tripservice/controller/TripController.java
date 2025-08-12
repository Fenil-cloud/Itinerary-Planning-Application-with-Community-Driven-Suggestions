package com.ltineraryplanning.tripservice.controller;

import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import com.ltineraryplanning.tripservice.dto.TripDTO;
import com.ltineraryplanning.tripservice.enums.StatusCodeEnum;
import com.ltineraryplanning.tripservice.service.TripService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trip/")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("createTrip")
    ResponseDTO createTrip(@Valid @RequestBody TripDTO tripDTO, Errors errors ,@RequestHeader("Authorization") String auth) throws ParseException {
        if(errors.hasErrors()){
            return new ResponseDTO(StatusCodeEnum.BAD_REQUEST.getStatusCode(),errors.getAllErrors().get(0).getDefaultMessage(),null);
        }
        else{
            return tripService.createTrip(tripDTO,auth);
        }
    }

    @PostMapping("{tripId}/share")
    ResponseDTO shareTrip(@PathVariable Long tripId, @RequestBody List<String> usernames){
        return tripService.shareTrip(tripId,usernames);
    }

    @PostMapping("notify/{tripId}")
    ResponseDTO notifyForUpComingTrip(@PathVariable Long tripId){
        return tripService.notifyForUpComingTrip(tripId);
    }

    @GetMapping("{tripId}")
    ResponseDTO getTripDetailsById(@PathVariable Long tripId){
        return tripService.getTripDetailsById(tripId);
    }


}
