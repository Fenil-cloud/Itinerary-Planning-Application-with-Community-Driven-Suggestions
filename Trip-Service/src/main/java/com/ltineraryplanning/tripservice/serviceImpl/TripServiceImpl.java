package com.ltineraryplanning.tripservice.serviceImpl;



import com.ltineraryplanning.CQRS.entity.DestinationView;
import com.ltineraryplanning.CQRS.entity.TripSharedEvent;
import com.ltineraryplanning.CQRS.entity.TripView;
import com.ltineraryplanning.CQRS.repository.CQRS_TripRepository;
import com.ltineraryplanning.tripservice.client.AuthServiceClient;
import com.ltineraryplanning.tripservice.constants.Constants;
import com.ltineraryplanning.tripservice.dto.*;
import com.ltineraryplanning.tripservice.entity.Destination;
import com.ltineraryplanning.tripservice.entity.Trip;
import com.ltineraryplanning.tripservice.enums.StatusCodeEnum;
import com.ltineraryplanning.tripservice.enums.TripType;
import com.ltineraryplanning.tripservice.exception.TripNotFoundException;
import com.ltineraryplanning.tripservice.repository.TripRepository;
import com.ltineraryplanning.tripservice.service.EsService;
import com.ltineraryplanning.tripservice.service.TripService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
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

    @Value("${kafkaTopic.createEventTopic}")
    private String createEventTopic;

//    @Value("${kafkaTopic.shareEventForUpdate}")
//    private String shareEventForUpdate;
//
//
//    @Value("${kafkaTopic.updateEventTopic}")
//    private String updateEventTopic;
    @Autowired
    private EsService esService;

    @Autowired
    private CQRS_TripRepository cqrs_tripRepository;

    @Autowired
    private QdrantVectorStore qdrantVectorStore;

    @Autowired
    private EmbeddingModel embeddingModel;
    @Override
    public ResponseDTO createTrip(TripDTO tripDTO,String auth) throws ParseException {
        List<String> stringList = new ArrayList<>();
        stringList.add(TripType.FAMILY_TRIP.toString());
        stringList.add(TripType.BUSINESS_TRIP.toString());
        stringList.add(TripType.SOLO_TRIP.toString());
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
       Trip saveTrip =  tripRepository.save(trip);
        EsSearchItineraryDTO esSearchItineraryDTO = EsSearchItineraryDTO.builder()
                .tripNames(Set.of(tripDTO.getTripName()))
                .build();
        esService.saveTestToElastic(esSearchItineraryDTO);
        StringBuilder tripText = new StringBuilder();
        tripText.append("Trip Name: ").append(saveTrip.getTripName()).append("\n");
        tripText.append("Trip Type: ").append(saveTrip.getTripType()).append("\n");
        tripText.append("Number of Members: ").append(saveTrip.getNumberOfMembers()).append("\n");
        tripText.append("Start Date: ").append(saveTrip.getStartDate()).append("\n");
        tripText.append("End Date: ").append(saveTrip.getEndDate()).append("\n");
        tripText.append("Destinations:\n");

        for (Destination dest : saveTrip.getDestinations()) {
            tripText.append(" - From: ").append(dest.getFrom())
                    .append(", To: ").append(dest.getTo())
                    .append(", StartDate: ").append(dest.getStartDate())
                    .append(", EndDate: ").append(dest.getEndDate())
                    .append("\n");
        }

//        List<Embedding> embeddings = embeddingModel.embed(List.of(tripText.toString()));
//        Embedding embedding = embeddings.get(0);

        float[] embedding = embeddingModel.embed(tripText.toString());
        // Store in Qdrant
        Document doc = new Document(
                tripText.toString(),   // content
                Map.of("tripId", saveTrip.getTripId().toString())  // metadata
        );
        qdrantVectorStore.add(List.of(doc));
        List<DestinationView> destinationViews = saveTrip.getDestinations().stream()
                .map(dest -> DestinationView.builder()
                        .destinationId(dest.getDestinationId())
                        .from(dest.getFrom())
                        .to(dest.getTo())
                        .startDate(dest.getStartDate())
                        .endDate(dest.getEndDate())
                        .createdAt(dest.getCreatedAt())
                        .updatedAt(dest.getUpdatedAt())
                        .startDate(dest.getStartDate())
                        .endDate(dest.getEndDate())
                        .tripId(dest.getTrip().getTripId())
                        .build())
                .collect(Collectors.toList());
        TripView tripView = TripView.builder()
                .numberOfMembers(saveTrip.getNumberOfMembers())
                .tripName(saveTrip.getTripName())
                .tripType(saveTrip.getTripType())
                .allowComment(saveTrip.getAllowComment())
                .createdAt(saveTrip.getCreatedAt())
                .endDate(saveTrip.getEndDate())
                .isPrivate(saveTrip.getIsPrivate())
                .isPublic(saveTrip.getIsPublic())
                .shareWithUsernames(saveTrip.getShareWithUsernames())
                .tripId(saveTrip.getTripId())
                .userId(saveTrip.getUserId())
                .updatedAt(saveTrip.getUpdatedAt())
                .startDate(saveTrip.getStartDate())
                .destinations(destinationViews)
                .build();
        kafkaTemplate.send(createEventTopic,tripView);
        log.info("kafka Template send message {} :" + tripView);
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(), Constants.TRIP_CREATED_SUCCESSFULLY,saveTrip.getTripId());
    }

    @Override
    public ResponseDTO shareTrip(Long tripId, List<String> usernames) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        trip.getShareWithUsernames().addAll(usernames);
        Trip savedTrip = tripRepository.save(trip);
        TripSharedEvent tripSharedEvent = TripSharedEvent.builder()
                .tripId(savedTrip.getTripId())
                .shareWithUsernames(savedTrip.getShareWithUsernames())
                .build();
        // Send event to Kafka
        kafkaTemplate.send(createEventTopic, tripSharedEvent);
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
//        todo uncomment for cqrs design pattern
        List<TripView> trip1 = cqrs_tripRepository.findAll();
        log.info("Data ::: {}",trip1);
//        if(trip)
//        log.info("Trip: data :: {}",trip.getTripName());
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        log.info("data :: {}",trip);
//        log.info("enum{});
//        log.info("{}",trip);
//        System.out.println(trip);
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


       Trip saveTrip =  tripRepository.save(trip1);
        List<DestinationView> destinationViews = saveTrip.getDestinations().stream()
                .map(dest -> DestinationView.builder()
                        .destinationId(dest.getDestinationId())
                        .from(dest.getFrom())
                        .to(dest.getTo())
                        .startDate(dest.getStartDate())
                        .endDate(dest.getEndDate())
                        .createdAt(dest.getCreatedAt())
                        .updatedAt(dest.getUpdatedAt())
                        .startDate(dest.getStartDate())
                        .endDate(dest.getEndDate())
                        .build())
                .collect(Collectors.toList());
                TripView tripView = TripView.builder()
                .numberOfMembers(saveTrip.getNumberOfMembers())
                .tripName(saveTrip.getTripName())
                .tripType(saveTrip.getTripType())
                .allowComment(saveTrip.getAllowComment())
                .createdAt(saveTrip.getCreatedAt())
                .endDate(saveTrip.getEndDate())
                .isPrivate(saveTrip.getIsPrivate())
                .isPublic(saveTrip.getIsPublic())
                .shareWithUsernames(saveTrip.getShareWithUsernames())
                .tripId(saveTrip.getTripId())
                .userId(saveTrip.getUserId())
                .updatedAt(saveTrip.getUpdatedAt())
                .startDate(saveTrip.getStartDate())
                .destinations(destinationViews)
                .build();
        kafkaTemplate.send(createEventTopic,tripView);
        return new ResponseDTO(StatusCodeEnum.OK.getStatusCode(),Constants.TRIP_UPDATED_SUCCESSFULLY,null);
    }

    @Override
    public ResponseDTO allTrip() {
        List<Trip> trips = tripRepository.findAll();
        return new ResponseDTO("200","all trips",trips);
    }


}
