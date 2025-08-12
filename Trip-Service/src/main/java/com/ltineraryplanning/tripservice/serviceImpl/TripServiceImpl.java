package com.ltineraryplanning.tripservice.serviceImpl;

import com.ltineraryplanning.tripservice.client.AuthServiceClient;
import com.ltineraryplanning.tripservice.constants.Constants;
import com.ltineraryplanning.tripservice.dto.*;
import com.ltineraryplanning.tripservice.entity.Destination;
import com.ltineraryplanning.tripservice.entity.Trip;
import com.ltineraryplanning.tripservice.enums.StatusCodeEnum;
import com.ltineraryplanning.tripservice.exception.TripNotFoundException;
import com.ltineraryplanning.tripservice.repository.TripRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


}
