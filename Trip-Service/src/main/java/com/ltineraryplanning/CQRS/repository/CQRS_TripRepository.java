package com.ltineraryplanning.CQRS.repository;

import com.ltineraryplanning.CQRS.entity.TripView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CQRS_TripRepository extends MongoRepository<TripView,Long> {

    TripView findByTripId(Long tripId);
}
