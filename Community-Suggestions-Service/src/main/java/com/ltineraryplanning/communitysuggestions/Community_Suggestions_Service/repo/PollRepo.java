package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer.PollDetailsDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PollRepo extends MongoRepository<PollDetailsDTO,String> {
    List<PollDetailsDTO> findBySuggestionId(String pollId);
}
