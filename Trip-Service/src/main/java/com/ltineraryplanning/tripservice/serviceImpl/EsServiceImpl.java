package com.ltineraryplanning.tripservice.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltineraryplanning.tripservice.constants.Constants;
import com.ltineraryplanning.tripservice.dto.EsSearchItineraryDTO;
import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import com.ltineraryplanning.tripservice.dto.elastic.EsQueryBuilderDTO;
import com.ltineraryplanning.tripservice.dto.elastic.HitsDTO;
import com.ltineraryplanning.tripservice.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

@Service
public class EsServiceImpl implements EsService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void saveTestToElastic(EsSearchItineraryDTO esSearchItineraryDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
        HttpEntity<EsSearchItineraryDTO> httpEntity = new HttpEntity<>(esSearchItineraryDTO, headers);
        restTemplate.exchange(Constants.SAVE_TRIP_NAME_TO_ES, HttpMethod.POST, httpEntity, Map.class);

    }

    @Override
    public ArrayList<EsSearchItineraryDTO> searchByTripNameInEs(String name) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        EsQueryBuilderDTO esQueryBuilder = new EsQueryBuilderDTO();
        String query = esQueryBuilder.searchByTripName(name);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
        HttpEntity<String> httpEntity = new HttpEntity<>(query, headers);
        Map data = (Map) restTemplate.exchange(Constants.TRIP_NAME_SEARCH_ES, HttpMethod.POST, httpEntity, Map.class).getBody().get("hits");
        String dataTestString = objectMapper.writeValueAsString(data);
        HitsDTO hitsDTO = objectMapper.readValue(dataTestString, HitsDTO.class);
        ArrayList<EsSearchItineraryDTO> itineraryInfos = new ArrayList<>();
        hitsDTO.getHits().stream().forEach(p -> itineraryInfos.add(p.get_source()));
        System.out.println(itineraryInfos);
        return itineraryInfos;
    }

    @Override
    public ResponseDTO searchTripName(String name) throws JsonProcessingException {
        return null;
    }
}
