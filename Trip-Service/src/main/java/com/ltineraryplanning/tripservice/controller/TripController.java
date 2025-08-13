package com.ltineraryplanning.tripservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import com.ltineraryplanning.tripservice.dto.TripDTO;
import com.ltineraryplanning.tripservice.enums.StatusCodeEnum;
import com.ltineraryplanning.tripservice.service.EsService;
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

    @Autowired
    private EsService esService;

    @PostMapping("createTrip")
    public ResponseDTO createTrip(@Valid @RequestBody TripDTO tripDTO, Errors errors ,@RequestHeader("Authorization") String auth) throws ParseException {
        if(errors.hasErrors()){
            return new ResponseDTO(StatusCodeEnum.BAD_REQUEST.getStatusCode(),errors.getAllErrors().get(0).getDefaultMessage(),null);
        }
        else{
            return tripService.createTrip(tripDTO,auth);
        }
    }

    @PostMapping("{tripId}/share")
    public ResponseDTO shareTrip(@PathVariable Long tripId, @RequestBody List<String> usernames){
        return tripService.shareTrip(tripId,usernames);
    }

    @PostMapping("notify/{tripId}")
    public ResponseDTO notifyForUpComingTrip(@PathVariable Long tripId){
        return tripService.notifyForUpComingTrip(tripId);
    }

    @GetMapping("{tripId}")
    public ResponseDTO getTripDetailsById(@PathVariable Long tripId){
        return tripService.getTripDetailsById(tripId);
    }

   @GetMapping("search")
   public ResponseDTO searchTripName(@RequestParam("tripName") String tripName) throws JsonProcessingException {
        return esService.searchTripName(tripName);
   }

   @GetMapping("searchPartial")
    public ResponseDTO searchPartialTrip(@RequestParam("tripName") String tripName) throws JsonProcessingException {
        return esService.searchWithPartialTripName(tripName);
   }

   @PutMapping("updateTrip/{tripId}")
    public ResponseDTO fullUpdateTrip(@Valid @RequestBody TripDTO tripDTO,@PathVariable("tripId") Long tripId,@RequestHeader("Authorization") String authHeader) throws ParseException {
        return tripService.updateTrip(tripDTO,tripId,authHeader,true);
   }

   @PatchMapping("updateTrip/{tripId}")
    public ResponseDTO partialUpdateTrip(@Valid @RequestBody TripDTO tripDTO,@PathVariable("tripId") Long tripId,@RequestHeader("Authorization") String authHeader) throws ParseException {
        return tripService.updateTrip(tripDTO,tripId,authHeader,false);
   }
}
