package com.ltineraryplanning.tripservice.dto.elastic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class EsQueryBuilderDTO {
    public String searchByTripName(String tripName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> match = new HashMap<>();
        match.put("tripNames", tripName);
        query.put("match", match);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("query", query);
        return objectMapper.writeValueAsString(queryMap);
    }
}
