package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo;


import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.DLQ;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DLQRepo extends MongoRepository<DLQ,String> {
}
