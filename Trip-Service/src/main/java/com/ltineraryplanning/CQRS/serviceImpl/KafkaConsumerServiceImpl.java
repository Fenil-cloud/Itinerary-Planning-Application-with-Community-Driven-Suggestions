package com.ltineraryplanning.CQRS.serviceImpl;



import com.ltineraryplanning.CQRS.entity.TripSharedEvent;
import com.ltineraryplanning.CQRS.entity.TripView;
import com.ltineraryplanning.CQRS.repository.CQRS_TripRepository;
import com.ltineraryplanning.CQRS.service.KafkaConsumerService;
import com.ltineraryplanning.tripservice.constants.Constants;
import com.ltineraryplanning.tripservice.exception.TripNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Autowired
    private CQRS_TripRepository cqrsTripRepository;


    //@KafkaListener(topics = "${kafkaTopic.sendEventTopic}")
    @KafkaListener(topics = "${kafkaTopic.createEventTopic}",groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consumeTripCreatedEvent(TripView tripView) {
        log.info(tripView.getTripName());
        cqrsTripRepository.save(tripView);
    }

    // @KafkaListener(topics = "${kafkaTopic.sendEventTopic}")
    @KafkaListener(topics = "${kafkaTopic.createEventTopic}",groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consumeTripSharedEvent(TripSharedEvent event) {
        TripView tripView = cqrsTripRepository.findById(event.getTripId())
                .orElseThrow(() -> new TripNotFoundException(Constants.TRIP_NOT_FOUND));
        tripView.getShareWithUsernames().addAll(event.getShareWithUsernames());
        cqrsTripRepository.save(tripView);
    }

    //@KafkaListener(topics = "${kafkaTopic.sendEventTopic}")
    @KafkaListener(topics = "${kafkaTopic.createEventTopic}",groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consumeUpdateTripEvent(TripView tripView) {
        cqrsTripRepository.save(tripView);
    }
}

