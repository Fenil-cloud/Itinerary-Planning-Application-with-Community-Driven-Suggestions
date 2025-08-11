package com.ltineraryplanning.tripservice.service;

import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import com.ltineraryplanning.tripservice.dto.TripDTO;
import jakarta.validation.Valid;

import java.text.ParseException;
import java.util.List;

public interface TripService {
    ResponseDTO createTrip(@Valid TripDTO tripDTO,String auth) throws ParseException;

    ResponseDTO shareTrip(Long tripId, List<String> usernames);


    ResponseDTO notifyForUpComingTrip(Long tripId);
}
