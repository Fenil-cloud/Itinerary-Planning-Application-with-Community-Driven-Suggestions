package com.ltineraryplanning.CQRS.repository;

import com.ltineraryplanning.CQRS.entity.TripView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CQRS_TripRepository extends MongoRepository<TripView,String> {
    List<TripView> findByUserIdAndIsDeleteFalse(String userId);
}
