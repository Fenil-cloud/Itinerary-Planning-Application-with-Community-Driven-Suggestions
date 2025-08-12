package com.ltineraryplanning.tripservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ltineraryplanning.tripservice.dto.EsSearchItineraryDTO;
import com.ltineraryplanning.tripservice.dto.ResponseDTO;

import java.util.ArrayList;

public interface EsService {
    void saveTestToElastic(EsSearchItineraryDTO esSearchItineraryDTO);
    ArrayList<EsSearchItineraryDTO> searchByTripNameInEs(String name) throws JsonProcessingException;

    ResponseDTO searchTripName(String name) throws JsonProcessingException;


}
