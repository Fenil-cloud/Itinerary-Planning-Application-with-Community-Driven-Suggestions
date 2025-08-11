package com.ltineraryplanning.tripservice.repository;

import com.ltineraryplanning.tripservice.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationRepository extends JpaRepository<Destination,Long> {
}
