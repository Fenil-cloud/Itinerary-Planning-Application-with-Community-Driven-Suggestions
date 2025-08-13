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

    public String searchByTripNamePartial(String tripName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Wildcard pattern for partial match
        String wildcardPattern = "*" + tripName.toLowerCase() + "*";

        Map<String, Object> wildcard = new HashMap<>();
        wildcard.put("value", wildcardPattern);
        wildcard.put("case_insensitive", true); // Optional

        Map<String, Object> tripNameMap = new HashMap<>();
        tripNameMap.put("tripNames", wildcard);

        Map<String, Object> query = new HashMap<>();
        query.put("wildcard", tripNameMap);

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("query", query);

        return objectMapper.writeValueAsString(queryMap);
    }
}
