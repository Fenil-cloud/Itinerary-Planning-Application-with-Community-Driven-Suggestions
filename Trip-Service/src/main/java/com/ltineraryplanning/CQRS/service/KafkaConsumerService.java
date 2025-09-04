package com.ltineraryplanning.CQRS.service;


import com.ltineraryplanning.CQRS.entity.TripSharedEvent;
import com.ltineraryplanning.CQRS.entity.TripView;

public interface KafkaConsumerService {
    void consumeTripCreatedEvent(TripView tripView);
    void consumeTripSharedEvent(TripSharedEvent event);
    void consumeUpdateTripEvent(TripView tripView);
    void consumeDeleteTripEvent(TripView tripView);
}
