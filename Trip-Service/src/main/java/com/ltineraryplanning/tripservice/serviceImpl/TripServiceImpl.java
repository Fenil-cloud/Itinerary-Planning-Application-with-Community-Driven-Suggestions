package com.ltineraryplanning.tripservice.serviceImpl;

import com.ltineraryplanning.tripservice.client.AuthServiceClient;
import com.ltineraryplanning.tripservice.constants.Constants;
import com.ltineraryplanning.tripservice.dto.*;
import com.ltineraryplanning.tripservice.entity.Destination;
import com.ltineraryplanning.tripservice.entity.Trip;
import com.ltineraryplanning.tripservice.enums.StatusCodeEnum;
import com.ltineraryplanning.tripservice.exception.TripNotFoundException;
import com.ltineraryplanning.tripservice.repository.TripRepository;
import com.ltineraryplanning.tripservice.service.EsService;
import com.ltineraryplanning.tripservice.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TripServiceImpl implements TripService {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ExtractTokenService extractTokenService;


    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Value("${kafkaTopic.trip}")
    private String topic;

    @Autowired
    private EsService esService;

    @Override
    public ResponseDTO createTrip(TripDTO tripDTO,String auth) throws ParseException {
    Map<String,Object> map =  extractTokenService.extractValue(auth);
       Trip trip = Trip.builder().build();
        BeanUtils.copyProperties(tripDTO, trip);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setUpdatedAt(null);
        trip.setUserId((String) map.get("preferred_username"));
        List<Destination> destinations = tripDTO.getDestinations().stream()
                .map(dto -> {
                    Destination dest = new Destination();
                    BeanUtils.copyProperties(dto, dest);
                    dest.setTrip(trip);
                    dest.setCreatedAt(LocalDateTime.now());
                    dest.setUpdatedAt(null);// maintain bidirectional mapping
                    return dest;
                })
                .collect(Collectors.toList());
        trip.setDestinations(destinations);
        tripRepository.save(trip);
        EsSearchItineraryDTO esSearchItineraryDTO = EsSearchItineraryDTO.builder()
                .tripNames(Set.of(tripDTO.getTripName()))
                .build();
        esService.saveTestToElastic(esSearchItineraryDTO);
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), Constants.TRIP_CREATED_SUCCESSFULLY,trip.getTripId());
    }

    @Override
    public ResponseDTO shareTrip(Long tripId, List<String> usernames) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        trip.getShareWithUsernames().addAll(usernames);
        tripRepository.save(trip);
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),Constants.TRIP_SHARED_SUCCESSFULLY,null);
    }

    @Override
    public ResponseDTO notifyForUpComingTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        List<String> usernames = new ArrayList<>();
        usernames.add(trip.getUserId());
        usernames.addAll(trip.getShareWithUsernames());
        System.err.println(usernames);
        List<EmailAndFirstNameDTO> emailAndFirstName = authServiceClient.getEmailAndFirstName(usernames);
        System.err.println(emailAndFirstName);
        List<SendDestinationInNotification> destinationList = trip.getDestinations().stream()
                .map(destination -> SendDestinationInNotification.builder()
                        .from(destination.getFrom())
                        .to(destination.getTo())
                        .startDate(destination.getStartDate())
                        .endDate(destination.getEndDate())
                        .build()
                )
                .collect(Collectors.toList());
        LocalDate tripStartDate = trip.getStartDate();
        LocalDate tripEndDate = trip.getEndDate();
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .emailAndFirstName(emailAndFirstName)
                .destinations(destinationList)
                .tripStartDate(tripStartDate)
                .tripEndDate(tripEndDate)
                .tripName(trip.getTripName())
                .build();
        Message<NotificationDTO> message = MessageBuilder
                .withPayload(notificationDTO)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
        kafkaTemplate.send(message);
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),Constants.UPCOMING_TRIP_NOTIFICATION_SENT_SUCCESSFULLY,null);
    }

    @Override
    public ResponseDTO getTripDetailsById(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),Constants.DATA_FETCHED_SUCCESSFULLY,trip);
    }

    @Override
    public ResponseDTO updateTrip(TripDTO tripDTO,Long tripId,String authHeader,boolean fullUpdate) throws ParseException {
        Map<String, Object> tokenData = extractTokenService.extractValue(authHeader);
        String username = (String) tokenData.get("preferred_username");
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        Optional<Trip> trip = tripRepository.findById(tripId);
        if(trip.isEmpty()){
            return new ResponseDTO(StatusCodeEnum.BAD_REQUEST.getStatusCode(),Constants.TRIP_NOT_FOUND ,null);
        }
        Trip trip1 = trip.get();
        boolean isAllowed = trip1.getUserId().equals(username) ||
                (trip1.getShareWithUsernames() != null && trip1.getShareWithUsernames().contains(username));
        if (!isAllowed) {
            return new ResponseDTO(StatusCodeEnum.UNAUTHORIZED.getStatusCode(),Constants.YOU_ARE_UNAUTHORIZED_TO_UPDATE_DETAIL,null);
        }
        if(fullUpdate) {
            trip1.setAllowComment(tripDTO.getAllowComment());
            trip1.setNumberOfMembers(tripDTO.getNumberOfMembers());
            trip1.setUpdatedAt(LocalDateTime.now());
            trip1.setTripName(tripDTO.getTripName());
            trip1.setStartDate(tripDTO.getStartDate());
            trip1.setEndDate(tripDTO.getEndDate());
            trip1.setIsPrivate(tripDTO.getIsPrivate());
            trip1.setIsPublic(tripDTO.getIsPublic());
            trip1.setTripType(tripDTO.getTripType());
        }else {
            if(trip1.getAllowComment()!= null) trip1.setAllowComment(tripDTO.getAllowComment());
            if(trip1.getNumberOfMembers()!= null)trip1.setNumberOfMembers(tripDTO.getNumberOfMembers());
            trip1.setUpdatedAt(LocalDateTime.now());
            if(trip1.getTripName()!= null)trip1.setTripName(tripDTO.getTripName());
            if(trip1.getStartDate()!=null)trip1.setStartDate(tripDTO.getStartDate());
            if(trip1.getEndDate() != null)trip1.setEndDate(tripDTO.getEndDate());
            if(trip1.getIsPrivate()!=null)trip1.setIsPrivate(tripDTO.getIsPrivate());
            if(trip1.getIsPublic()!=null)trip1.setIsPublic(tripDTO.getIsPublic());
            if(trip1.getTripType()!=null)trip1.setTripType(tripDTO.getTripType());
        }
        if (tripDTO.getDestinations() != null) {
            if (fullUpdate) {
                // Replace all destinations
                trip1.getDestinations().clear();
                for (DestinationDTO destDto : tripDTO.getDestinations()) {
                    trip1.getDestinations().add(Destination.builder()
                            .from(destDto.getFrom())
                            .to(destDto.getTo())
                            .startDate(destDto.getStartDate())
                            .endDate(destDto.getEndDate())
                            .trip(trip1)
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
            } else {
                // Partial update: Append new destinations without removing old ones
                for (DestinationDTO destDto : tripDTO.getDestinations()) {
                    trip1.getDestinations().add(Destination.builder()
                            .from(destDto.getFrom())
                            .to(destDto.getTo())
                            .startDate(destDto.getStartDate())
                            .endDate(destDto.getEndDate())
                            .trip(trip1)
                            .updatedAt(LocalDateTime.now())
                            .build());
                }
            }
        }

        tripRepository.save(trip1);
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),Constants.TRIP_UPDATED_SUCCESSFULLY,null);
    }


}
