package com.ltineraryplanning.tripservice.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltineraryplanning.tripservice.constants.Constants;
import com.ltineraryplanning.tripservice.dto.DestinationDTO;
import com.ltineraryplanning.tripservice.dto.EsSearchItineraryDTO;
import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import com.ltineraryplanning.tripservice.dto.SearchResponseDTO;
import com.ltineraryplanning.tripservice.dto.elastic.EsQueryBuilderDTO;
import com.ltineraryplanning.tripservice.dto.elastic.HitsDTO;
import com.ltineraryplanning.tripservice.entity.Trip;
import com.ltineraryplanning.tripservice.enums.StatusCodeEnum;
import com.ltineraryplanning.tripservice.repository.TripRepository;
import com.ltineraryplanning.tripservice.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EsServiceImpl implements EsService {
    private static final String REDIS_TRIP_SEARCH_PREFIX = "TRIP_SEARCH:";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TripRepository tripRepository;

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
        String redisKey = REDIS_TRIP_SEARCH_PREFIX + name;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            // logger.info("Returning data from Redis cache for hotelName: {}", hotelName);
            String cachedData = (String) redisTemplate.opsForValue().get(redisKey);
            return objectMapper.readValue(cachedData, ResponseDTO.class);
        }

        ArrayList<EsSearchItineraryDTO> arrayListOfRestaurantName = searchByTripNameInEs(name);
        System.out.println(arrayListOfRestaurantName);
        if (!arrayListOfRestaurantName.isEmpty()) {
            // Step 1: Flatten the list of sets into a list of strings
            List<String> restaurantNames = arrayListOfRestaurantName.stream()
                    .flatMap(info -> info.getTripNames().stream())
                    .collect(Collectors.toList());

            // Step 2: Match with DB — for example, using a JPA repository
            List<Trip> tripsFromDB = tripRepository.findByTripNameIn(restaurantNames);

            List<SearchResponseDTO> responseList = tripsFromDB.stream()
                    .map(trip -> SearchResponseDTO.builder()
                            .tripName(trip.getTripName())
                            .userId(trip.getUserId())
                            .isPrivate(trip.getIsPrivate())
                            .isPublic(trip.getIsPublic())
                            .startDate(trip.getStartDate())
                            .endDate(trip.getEndDate())
                            .numberOfMembers(trip.getNumberOfMembers())
                            .tripType(trip.getTripType())
                            .destinations(
                                    trip.getDestinations().stream()
                                            .map(dest -> DestinationDTO.builder()
                                                    .from(dest.getFrom())
                                                    .to(dest.getTo())
                                                    .startDate(dest.getStartDate())
                                                    .endDate(dest.getEndDate())
                                                    .build())
                                            .collect(Collectors.toList())
                            )
                            .build())
                    .collect(Collectors.toList());
            ResponseDTO response = new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), Constants.DATA_FETCHED_SUCCESSFULLY, responseList);

            redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(response));

            return response;

            // Step 3: Return response
//            return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), Constants.DATA_FETCHED_SUCCESSFULLY, responseList);
        }
        return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), Constants.TRIP_NOT_FOUND, null);
    }

    @Override
    public ResponseDTO searchWithPartialTripName(String name) throws JsonProcessingException {
        String redisKey = REDIS_TRIP_SEARCH_PREFIX + name;

        // Step 1: Check cache
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            String cachedData = (String) redisTemplate.opsForValue().get(redisKey);
            return objectMapper.readValue(cachedData, ResponseDTO.class);
        }

        // Step 2: Query Elasticsearch with wildcard
        EsQueryBuilderDTO esQueryBuilder = new EsQueryBuilderDTO();
        String query = esQueryBuilder.searchByTripNamePartial(name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(query, headers);

        Map data = (Map) restTemplate.exchange(
                Constants.TRIP_NAME_SEARCH_ES,
                HttpMethod.POST,
                httpEntity,
                Map.class
        ).getBody().get("hits");

        String dataTestString = objectMapper.writeValueAsString(data);
        HitsDTO hitsDTO = objectMapper.readValue(dataTestString, HitsDTO.class);

        ArrayList<EsSearchItineraryDTO> itineraryInfos = new ArrayList<>();
        hitsDTO.getHits().forEach(p -> itineraryInfos.add(p.get_source()));

        if (itineraryInfos.isEmpty()) {
            return new ResponseDTO(StatusCodeEnum.NOT_FOUND.getStatusCode(), Constants.TRIP_NOT_FOUND, null);
        }

        // Step 3: Extract matched trip names
        List<String> matchedTripNames = itineraryInfos.stream()
                .flatMap(info -> info.getTripNames().stream())
                .collect(Collectors.toList());

        // Step 4: Fetch details from DB
        List<Trip> tripsFromDB = tripRepository.findByTripNameIn(matchedTripNames);

        List<SearchResponseDTO> responseList = tripsFromDB.stream()
                .map(trip -> SearchResponseDTO.builder()
                        .tripName(trip.getTripName())
                        .userId(trip.getUserId())
                        .isPrivate(trip.getIsPrivate())
                        .isPublic(trip.getIsPublic())
                        .startDate(trip.getStartDate())
                        .endDate(trip.getEndDate())
                        .numberOfMembers(trip.getNumberOfMembers())
                        .tripType(trip.getTripType())
                        .destinations(
                                trip.getDestinations().stream()
                                        .map(dest -> DestinationDTO.builder()
                                                .from(dest.getFrom())
                                                .to(dest.getTo())
                                                .startDate(dest.getStartDate())
                                                .endDate(dest.getEndDate())
                                                .build())
                                        .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());

        ResponseDTO response = new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), Constants.DATA_FETCHED_SUCCESSFULLY, responseList);

        // Step 5: Cache result in Redis
        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(response));

        return response;
    }
}
