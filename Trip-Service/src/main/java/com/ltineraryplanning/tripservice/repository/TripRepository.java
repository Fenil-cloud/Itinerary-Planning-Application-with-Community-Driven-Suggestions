package com.ltineraryplanning.tripservice.repository;

import com.ltineraryplanning.tripservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {
    List<Trip> findByTripNameIn(List<String> restaurantNames);
//    List<Trip> find
}
